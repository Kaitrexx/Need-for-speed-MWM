package com.winlator.core;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.LinkAddress;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.RouteInfo;
import android.system.OsConstants;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

import com.winlator.NfsSettingsActivity;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

public class NetworkHelper {
    private final Context context;
    private final ConnectivityManager connectivityManager;

    public static class IFAddress {
        public String name = "eth0";
        public int flags = 0;
        public int family = OsConstants.AF_INET;
        public int scopeId = 0;
        public String address = "0";
        public String netmask = "0";

        @NonNull
        @Override
        public String toString() {
            return name+","+flags+","+family+","+scopeId+","+address+","+netmask;
        }
    }

    public NetworkHelper(Context context) {
        Context appContext = context.getApplicationContext();
        this.context = appContext != null ? appContext : context;
        connectivityManager = (ConnectivityManager)this.context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    private String getLanMode() {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(NfsSettingsActivity.PREF_LAN_MODE, "auto");
    }

    // Order: overlay VPN (ZeroTier etc., no IPv4 default route) first, then regular networks (Wi-Fi, Ethernet, mobile), then full-tunnel VPNs.
    // The first network becomes eth0: LAN games broadcast and advertise their IP only on it, so ZeroTier must be first to make LAN play work over it.
    private List<Network> getOrderedNetworks() {
        ArrayList<Network> overlay = new ArrayList<>();
        ArrayList<Network> regular = new ArrayList<>();
        ArrayList<Network> tunnel = new ArrayList<>();

        Network[] networks = connectivityManager != null ? connectivityManager.getAllNetworks() : null;
        if (networks != null) {
            for (Network network : networks) {
                LinkProperties linkProperties = connectivityManager.getLinkProperties(network);
                if (linkProperties == null) continue;
                if (isVpn(network)) {
                    if (hasIPv4DefaultRoute(linkProperties)) tunnel.add(network);
                    else overlay.add(network);
                }
                else regular.add(network);
            }
        }

        ArrayList<Network> result = new ArrayList<>(overlay);
        result.addAll(regular);
        result.addAll(tunnel);
        return result;
    }

    private boolean hasIPv4DefaultRoute(LinkProperties linkProperties) {
        for (RouteInfo route : linkProperties.getRoutes()) {
            if (route.isDefaultRoute() && route.getDestination().getAddress() instanceof Inet4Address) return true;
        }
        return false;
    }

    private boolean isVpn(Network network) {
        return hasTransport(network, NetworkCapabilities.TRANSPORT_VPN);
    }

    private boolean hasTransport(Network network, int transport) {
        if (connectivityManager == null) return false;
        NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
        return capabilities != null && capabilities.hasTransport(transport);
    }

    private static IFAddress createIPv4IFAddress(String name, String address, int prefixLength) {
        IFAddress ifAddress = new IFAddress();
        ifAddress.name = name;
        ifAddress.flags = OsConstants.IFF_UP | OsConstants.IFF_RUNNING | OsConstants.IFF_MULTICAST | OsConstants.IFF_BROADCAST;
        ifAddress.family = OsConstants.AF_INET;
        ifAddress.scopeId = 0;
        ifAddress.address = address;
        ifAddress.netmask = formatNetmask(prefixLength, false);
        return ifAddress;
    }

    private static IFAddress extractFirstIPv4IFAddress(LinkProperties linkProperties) {
        if (linkProperties == null) return null;
        for (LinkAddress linkAddress : linkProperties.getLinkAddresses()) {
            InetAddress address = linkAddress.getAddress();
            if (address instanceof Inet4Address && !address.isLoopbackAddress()) {
                return createIPv4IFAddress("eth0", address.getHostAddress(), linkAddress.getPrefixLength());
            }
        }
        return null;
    }

    private static IFAddress findInterfaceIPv4ByPrefix(String... prefixes) {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            if (interfaces == null) return null;
            ArrayList<NetworkInterface> list = Collections.list(interfaces);

            String[] searchOrder = (prefixes != null && prefixes.length > 0) ? prefixes : new String[]{""};
            for (String prefix : searchOrder) {
                for (NetworkInterface nif : list) {
                    try {
                        if (!nif.isUp() || nif.isLoopback()) continue;
                        String name = nif.getName();
                        if (name == null) continue;
                        if (!prefix.isEmpty() && !name.toLowerCase().startsWith(prefix)) continue;

                        for (InterfaceAddress ia : nif.getInterfaceAddresses()) {
                            InetAddress address = ia.getAddress();
                            if (address instanceof Inet4Address && !address.isLoopbackAddress()) {
                                int prefixLen = ia.getNetworkPrefixLength();
                                if (prefixLen <= 0 || prefixLen > 32) prefixLen = 24;
                                return createIPv4IFAddress("eth0", address.getHostAddress(), prefixLen);
                            }
                        }
                    }
                    catch (Exception ignored) {}
                }
            }
        }
        catch (Exception ignored) {}
        return null;
    }

    private IFAddress findOverlayIFAddress() {
        Network[] networks = connectivityManager != null ? connectivityManager.getAllNetworks() : null;
        if (networks != null) {
            for (Network network : networks) {
                if (!isVpn(network)) continue;
                LinkProperties lp = connectivityManager.getLinkProperties(network);
                if (lp == null || hasIPv4DefaultRoute(lp)) continue;
                IFAddress addr = extractFirstIPv4IFAddress(lp);
                if (addr != null) return addr;
            }
            // Fallback: VPN with default route (e.g. ZeroTier "Route all traffic" enabled)
            for (Network network : networks) {
                if (!isVpn(network)) continue;
                IFAddress addr = extractFirstIPv4IFAddress(connectivityManager.getLinkProperties(network));
                if (addr != null) return addr;
            }
        }
        return findInterfaceIPv4ByPrefix("zt", "tun", "tap", "ppp");
    }

    private IFAddress findWifiIFAddress() {
        Network[] networks = connectivityManager != null ? connectivityManager.getAllNetworks() : null;
        if (networks != null) {
            for (int transport : new int[]{NetworkCapabilities.TRANSPORT_WIFI, NetworkCapabilities.TRANSPORT_ETHERNET}) {
                for (Network network : networks) {
                    if (!hasTransport(network, transport)) continue;
                    IFAddress addr = extractFirstIPv4IFAddress(connectivityManager.getLinkProperties(network));
                    if (addr != null) return addr;
                }
            }
        }
        return findInterfaceIPv4ByPrefix("wlan", "ap", "swlan", "eth");
    }

    private IFAddress findFirstAvailableIPv4IFAddress() {
        if (connectivityManager != null) {
            for (Network network : getOrderedNetworks()) {
                IFAddress addr = extractFirstIPv4IFAddress(connectivityManager.getLinkProperties(network));
                if (addr != null) return addr;
            }
        }
        return findInterfaceIPv4ByPrefix((String[]) null);
    }

    public IFAddress getPreferredLanIFAddress(String mode) {
        if ("off".equals(mode)) return null;
        if ("zerotier".equals(mode)) {
            IFAddress overlay = findOverlayIFAddress();
            return overlay != null ? overlay : findFirstAvailableIPv4IFAddress();
        }
        if ("wifi".equals(mode)) {
            IFAddress wifi = findWifiIFAddress();
            return wifi != null ? wifi : findFirstAvailableIPv4IFAddress();
        }

        IFAddress overlay = findOverlayIFAddress();
        if (overlay != null) return overlay;
        IFAddress wifi = findWifiIFAddress();
        if (wifi != null) return wifi;
        return findFirstAvailableIPv4IFAddress();
    }

    public String getIPv4Address() {
        String mode = getLanMode();
        if (!"off".equals(mode)) {
            IFAddress preferred = getPreferredLanIFAddress(mode);
            if (preferred != null) return preferred.address;
        }
        IFAddress fallback = findFirstAvailableIPv4IFAddress();
        return fallback != null ? fallback.address : null;
    }

    // NFS_LAN: ZeroTier / overlay VPN interfeysinin IP-si
    public String getOverlayIPv4Address() {
        IFAddress addr = findOverlayIFAddress();
        return addr != null ? addr.address : null;
    }

    // NFS_LAN: WiFi / Hotspot interfeysinin IP-si
    public String getWifiIPv4Address() {
        IFAddress addr = findWifiIFAddress();
        return addr != null ? addr.address : null;
    }

    public List<IFAddress> getIFAddresses() {
        String mode = getLanMode();
        if (!"off".equals(mode)) {
            IFAddress preferred = getPreferredLanIFAddress(mode);
            if (preferred != null) {
                ArrayList<IFAddress> single = new ArrayList<>(1);
                single.add(preferred);
                return single;
            }
        }

        ArrayList<IFAddress> result = new ArrayList<>();

        int index = 0;
        for (Network network : getOrderedNetworks()) {
            LinkProperties linkProperties = connectivityManager.getLinkProperties(network);
            if (linkProperties == null) continue;

            // First network becomes eth0, the following ones eth1, eth2...
            String name = "eth"+index;
            boolean added = false;

            for (LinkAddress linkAddress : linkProperties.getLinkAddresses()) {
                InetAddress address = linkAddress.getAddress();
                if (!(address instanceof Inet4Address || address instanceof Inet6Address)) continue;
                if (address.isLoopbackAddress()) continue;

                IFAddress ifAddress = new IFAddress();
                ifAddress.name = name;
                ifAddress.flags = OsConstants.IFF_UP | OsConstants.IFF_RUNNING | OsConstants.IFF_MULTICAST;

                if (address instanceof Inet6Address) {
                    ifAddress.family = OsConstants.AF_INET6;
                    ifAddress.scopeId = ((Inet6Address)address).getScopeId();
                }
                else ifAddress.flags |= OsConstants.IFF_BROADCAST;

                ifAddress.address = address.getHostAddress();
                ifAddress.netmask = formatNetmask(linkAddress.getPrefixLength(), address instanceof Inet6Address);

                result.add(ifAddress);
                added = true;
            }
            if (added) index++;
        }
        return result;
    }

    public static String formatIpAddress(int ipAddress) {
        return (ipAddress & 255)+"."+((ipAddress >> 8) & 255)+"."+((ipAddress >> 16) & 255)+"."+((ipAddress >> 24) & 255);
    }

    public static String formatNetmask(int prefixLength) {
        return formatNetmask(prefixLength, prefixLength > 32);
    }

    // Works for any prefix length (/20, /22 ...), not only 8/16/24/32/64.
    public static String formatNetmask(int prefixLength, boolean ipv6) {
        if (!ipv6) {
            if (prefixLength < 0) prefixLength = 0;
            if (prefixLength > 32) prefixLength = 32;
            long mask = prefixLength == 0 ? 0 : (0xffffffffL << (32 - prefixLength)) & 0xffffffffL;
            return ((mask >> 24) & 255)+"."+((mask >> 16) & 255)+"."+((mask >> 8) & 255)+"."+(mask & 255);
        }

        if (prefixLength == 64) return "ffff:ffff:ffff:ffff::"; // same string as before
        if (prefixLength < 0) prefixLength = 0;
        if (prefixLength > 128) prefixLength = 128;
        StringBuilder sb = new StringBuilder();
        int remaining = prefixLength;
        for (int i = 0; i < 8; i++) {
            int bits = Math.min(16, Math.max(0, remaining));
            remaining -= bits;
            int group = bits == 0 ? 0 : (0xffff << (16 - bits)) & 0xffff;
            if (i > 0) sb.append(":");
            sb.append(Integer.toHexString(group));
        }
        return sb.toString();
    }

    public boolean isConnected() {
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null) return false;
        int type = networkInfo.getType();
        return networkInfo.isAvailable() && networkInfo.isConnectedOrConnecting() && (type == ConnectivityManager.TYPE_WIFI || type == ConnectivityManager.TYPE_ETHERNET  || type == ConnectivityManager.TYPE_MOBILE);
    }
}
