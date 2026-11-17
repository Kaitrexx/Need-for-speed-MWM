package com.winlator.inputcontrols;

// NFS_SCHEME: NFS MW 2005-in bütün idarə funksiyaları və onların default
// klaviatura bağlamaları (oyunun Options → Controls ekranından götürülüb).
// NFS_FIX: koordinatlar qırmızı xətlərin (oyun sahəsinin) içərisinə çəkildi,
// hər funksiyaya fərqli ikon verildi.
public class NfsControlScheme {
    public final String name;
    public final String binding;
    public final int iconId;
    public final float x;
    public final float y;
    public final String group;

    public NfsControlScheme(String name, String binding, int iconId, float x, float y, String group) {
        this.name = name;
        this.binding = binding;
        this.iconId = iconId;
        this.x = x;
        this.y = y;
        this.group = group;
    }

    public static final String GROUP_DRIVE = "SÜRÜŞ";
    public static final String GROUP_CAMERA = "KAMERA";
    public static final String GROUP_MENU = "MENYU";

    // Manual gear: Shift Up = Shift, Shift Down = Ctrl, Speedbreaker = G, Nitro = X
    public static NfsControlScheme[] all() {
        return new NfsControlScheme[] {
            // SÜRÜŞ (fərqli ikonlar: 22,23,20,21,26,25,24)
            new NfsControlScheme("Qaz (Accelerate)", "KEY_UP", 22, 0.82f, 0.58f, GROUP_DRIVE),
            new NfsControlScheme("Əyləc / Geri", "KEY_DOWN", 23, 0.72f, 0.76f, GROUP_DRIVE),
            new NfsControlScheme("Sola dön", "KEY_LEFT", 20, 0.18f, 0.70f, GROUP_DRIVE),
            new NfsControlScheme("Sağa dön", "KEY_RIGHT", 21, 0.30f, 0.70f, GROUP_DRIVE),
            new NfsControlScheme("Əl əyləci (Handbrake)", "KEY_SPACE", 26, 0.58f, 0.80f, GROUP_DRIVE),
            new NfsControlScheme("Speedbreaker", "KEY_G", 25, 0.72f, 0.46f, GROUP_DRIVE),
            new NfsControlScheme("Nitro (N2O)", "KEY_X", 24, 0.88f, 0.32f, GROUP_DRIVE),
            new NfsControlScheme("Gear ↑ (Shift Up)", "KEY_SHIFT_L", 0, 0.60f, 0.42f, GROUP_DRIVE),
            new NfsControlScheme("Gear ↓ (Shift Down)", "KEY_CTRL_L", 1, 0.60f, 0.58f, GROUP_DRIVE),
            // KAMERA (fərqli ikonlar: 2,3,4)
            new NfsControlScheme("Kameranı dəyiş", "KEY_C", 2, 0.44f, 0.16f, GROUP_CAMERA),
            new NfsControlScheme("Geri bax", "KEY_L", 3, 0.54f, 0.16f, GROUP_CAMERA),
            new NfsControlScheme("Kameranı geri çək", "KEY_P", 4, 0.64f, 0.16f, GROUP_CAMERA),
            // MENYU / DIGƏR (fərqli ikonlar: 5..11)
            new NfsControlScheme("Maşını sıfırla", "KEY_R", 5, 0.30f, 0.22f, GROUP_MENU),
            new NfsControlScheme("Hadisəni başlat", "KEY_ENTER", 6, 0.30f, 0.34f, GROUP_MENU),
            new NfsControlScheme("Qara siyahı", "KEY_B", 7, 0.30f, 0.46f, GROUP_MENU),
            new NfsControlScheme("Dünya xəritəsi", "KEY_M", 8, 0.30f, 0.58f, GROUP_MENU),
            new NfsControlScheme("SMS mesajları", "KEY_TAB", 9, 0.30f, 0.70f, GROUP_MENU),
            new NfsControlScheme("Növbəti mahnı", "KEY_T", 10, 0.30f, 0.82f, GROUP_MENU),
            new NfsControlScheme("Pauza", "KEY_ESC", 11, 0.22f, 0.16f, GROUP_MENU),
        };
    }
}
