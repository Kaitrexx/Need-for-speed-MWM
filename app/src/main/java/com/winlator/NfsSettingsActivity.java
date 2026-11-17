package com.winlator;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.winlator.core.AppUtils;
import com.winlator.core.KeyValueSet;
import com.winlator.core.LocaleHelper;
import com.winlator.winhandler.GamepadHandler;

import java.util.ArrayList;
import java.util.List;

public class NfsSettingsActivity extends AppCompatActivity {
    public static final String PREF_SCREEN_SIZE = "nfs_screen_size";
    public static final String PREF_ASPECT = "nfs_aspect";
    public static final String PREF_FPS_LIMIT = "nfs_fps_limit";
    public static final String PREF_HUD_MODE = "nfs_hud_mode";
    public static final String PREF_FULLSCREEN = "nfs_fullscreen";
    public static final String DEFAULT_SCREEN_SIZE = "640x480";
    public static final String PREF_GPU_DRIVER = "nfs_gpu_driver"; // NFS_GPU_V1
    private static final String[] GPU_VALUES = {"auto", "turnip", "vortek"};
    public static final String PREF_CPU_CORES = "nfs_cpu_cores"; // NFS_CPU_V1
    // NFS_UNIVERSAL: "auto" = cihazın ən sürətli 2 nüvəsini sysfs-dən tapır
    private static final String[] CPU_VALUES = {"auto", "6,7", "4,5,6,7", "0,1,2,3,4,5,6,7"};

    // NFSMW-in öz ölçüləri (speed.exe-dən): yalnız 4:3 və 5:4.
    private static final String[] ASPECT_LABELS = {"4:3", "5:4"};
    private static final String[] ASPECT_VALUES = {"4:3", "5:4"};
    private static final String[] SIZES_43 = {"640x480", "800x600", "1024x768", "1280x960", "1600x1200"};
    private static final String[] SIZES_54 = {"1280x1024"};
    // NFS_OPT: 90Hz ekran üçün 45 əlavə edildi (90/2 = judder yox, az istilik)
    private static final int[] FPS_VALUES = {0, 30, 45, 60};
    public static final String PREF_BOX64 = "nfs_box64_preset";
    private static final String[] BOX64_VALUES = {"PERFORMANCE", "INTERMEDIATE", "STABILITY"};
    // NFS_PERF: dynarec bloklarını diska yaz (təkrar açılışda kompilyasiya spike azalır)
    public static final String PREF_DYNACACHE = "nfs_dynacache";
    public static final String PREF_DXVK = "nfs_dxvk_version";
    // NFS_UNIVERSAL: "auto" = Vulkan API versiyasına görə 1.10.3 / 2.4.1 seçir
    private static final String[] DXVK_VALUES = {"auto", "1.10.3", "2.4.1"};
    // NFS_LAN: oyunun server.cfg faylına hansı IP-ni yazacağımızı seçir
    public static final String PREF_LAN_MODE = "nfs_lan_mode";
    private static final String[] LAN_MODE_VALUES = {"auto", "zerotier", "wifi", "off"};

    // NFS_SET_V1: tünd/sarı dizayn
    private static final int YELLOW = 0xFFFFC107;
    private static final int BG = 0xFF0B0B0D;
    private static final int CARD = 0xFF131315;
    private static final int LINE = 0xFF2A2A2E;
    private static final int MUTED = 0xFFB4B4B4;

    private final List<String> sizeValues = new ArrayList<>();
    private final List<String> sizeLabels = new ArrayList<>();
    private Spinner sizeSpinner;
    private SharedPreferences preferences;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.setSystemLocale(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppUtils.setActivityTheme(this);
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE); // yalnız landscape
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        applyImmersive();
        getWindow().setStatusBarColor(BG);
        getWindow().setNavigationBarColor(BG);

        final int match = ViewGroup.LayoutParams.MATCH_PARENT;
        final int wrap = ViewGroup.LayoutParams.WRAP_CONTENT;

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(BG);

        // Başlıq
        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setBackgroundColor(0xFF141416);
        header.setPadding(dp(40), dp(8), dp(40), dp(8));

        TextView back = new TextView(this);
        back.setText("\u2039");
        back.setTextSize(28);
        back.setTextColor(YELLOW);
        back.setGravity(Gravity.CENTER);
        back.setContentDescription(getString(R.string.nfs_set_back));
        GradientDrawable backBg = new GradientDrawable();
        backBg.setColor(0xFF141416);
        backBg.setStroke(dp(2), YELLOW);
        back.setBackground(backBg);
        back.setOnClickListener((v) -> finish());
        header.addView(back, new LinearLayout.LayoutParams(dp(44), dp(44)));

        TextView title = new TextView(this);
        title.setText(R.string.nfs_set_title);
        title.setTextSize(22);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setTextColor(0xFFFFFFFF);
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(0, wrap, 1f);
        titleParams.leftMargin = dp(16);
        header.addView(title, titleParams);

        TextView subtitle = new TextView(this);
        subtitle.setText(R.string.nfs_set_subtitle);
        subtitle.setTextSize(13);
        subtitle.setTextColor(0xFFE6E6E6);
        header.addView(subtitle, new LinearLayout.LayoutParams(wrap, wrap));
        root.addView(header, new LinearLayout.LayoutParams(match, wrap));

        View yellowLine = new View(this);
        yellowLine.setBackgroundColor(YELLOW);
        root.addView(yellowLine, new LinearLayout.LayoutParams(match, dp(3)));

        // Gövdə: iki sütun
        LinearLayout body = new LinearLayout(this);
        body.setOrientation(LinearLayout.HORIZONTAL);
        body.setPadding(dp(40), dp(16), dp(40), dp(8));
        LinearLayout left = new LinearLayout(this);
        left.setOrientation(LinearLayout.VERTICAL);
        LinearLayout right = new LinearLayout(this);
        right.setOrientation(LinearLayout.VERTICAL);
        body.addView(left, new LinearLayout.LayoutParams(0, wrap, 1f));
        LinearLayout.LayoutParams rightParams = new LinearLayout.LayoutParams(0, wrap, 1f);
        rightParams.leftMargin = dp(16);
        body.addView(right, rightParams);

        // QRAFİKA
        String savedSize = preferences.getString(PREF_SCREEN_SIZE, DEFAULT_SCREEN_SIZE);
        String aspect = savedSize.equals("1280x1024") ? "5:4" : "4:3";
        fillSizes(aspect);
        int sizeIndex = Math.max(0, sizeValues.indexOf(savedSize));
        int aspectIndex = aspect.equals("5:4") ? 1 : 0;

        LinearLayout graphics = addCard(left, getString(R.string.nfs_set_graphics));
        addSpinnerRow(graphics, getString(R.string.nfs_set_aspect), ASPECT_LABELS, aspectIndex, (position) -> applyAspect(ASPECT_VALUES[position]));
        sizeSpinner = addSpinnerRow(graphics, getString(R.string.nfs_set_resolution), sizeLabels.toArray(new String[0]), sizeIndex, (position) -> {
            if (position >= 0 && position < sizeValues.size())
                preferences.edit().putString(PREF_SCREEN_SIZE, sizeValues.get(position)).apply();
        });

        Switch swFullscreen = new Switch(this);
        swFullscreen.setChecked(preferences.getBoolean(PREF_FULLSCREEN, true));
        swFullscreen.setThumbTintList(new ColorStateList(
            new int[][]{{android.R.attr.state_checked}, {}}, new int[]{YELLOW, 0xFF8A8A8E}));
        swFullscreen.setTrackTintList(new ColorStateList(
            new int[][]{{android.R.attr.state_checked}, {}}, new int[]{0x99FFC107, 0xFF3A3A3E}));
        swFullscreen.setOnCheckedChangeListener((button, checked) ->
            preferences.edit().putBoolean(PREF_FULLSCREEN, checked).apply());
        addRow(graphics, getString(R.string.nfs_set_fullscreen), swFullscreen);

        // GPU SÜRÜCÜSÜ
        LinearLayout gpuCard = addCard(left, getString(R.string.nfs_set_gpu_title));
        String savedGpu = preferences.getString(PREF_GPU_DRIVER, "auto");
        int gpuIndex = 0;
        for (int i = 0; i < GPU_VALUES.length; i++) if (GPU_VALUES[i].equals(savedGpu)) gpuIndex = i;
        addSpinnerRow(gpuCard, getString(R.string.nfs_set_gpu_driver), getResources().getStringArray(R.array.nfs_gpu_labels), gpuIndex, (position) ->
            preferences.edit().putString(PREF_GPU_DRIVER, GPU_VALUES[position]).apply());

        // PERFORMANS
        LinearLayout perf = addCard(right, getString(R.string.nfs_set_perf));
        String savedCpu = preferences.getString(PREF_CPU_CORES, CPU_VALUES[0]);
        int cpuIndex = 0;
        for (int i = 0; i < CPU_VALUES.length; i++) if (CPU_VALUES[i].equals(savedCpu)) cpuIndex = i;
        addSpinnerRow(perf, getString(R.string.nfs_set_cpu), getResources().getStringArray(R.array.nfs_cpu_labels), cpuIndex, (position) ->
            preferences.edit().putString(PREF_CPU_CORES, CPU_VALUES[position]).apply());

        String savedBox64 = preferences.getString(PREF_BOX64, BOX64_VALUES[0]);
        int box64Index = 0;
        for (int i = 0; i < BOX64_VALUES.length; i++) if (BOX64_VALUES[i].equals(savedBox64)) box64Index = i;
        addSpinnerRow(perf, getString(R.string.nfs_set_box64), getResources().getStringArray(R.array.nfs_box64_labels), box64Index, (position) ->
            preferences.edit().putString(PREF_BOX64, BOX64_VALUES[position]).apply());

        String savedDxvk = preferences.getString(PREF_DXVK, DXVK_VALUES[0]);
        int dxvkIndex = 0;
        for (int i = 0; i < DXVK_VALUES.length; i++) if (DXVK_VALUES[i].equals(savedDxvk)) dxvkIndex = i;
        addSpinnerRow(perf, getString(R.string.nfs_set_dxvk), getResources().getStringArray(R.array.nfs_dxvk_labels), dxvkIndex, (position) ->
            preferences.edit().putString(PREF_DXVK, DXVK_VALUES[position]).apply());

        // NFS_PERF: Box64 dynacache aç/bağla (default açıq)
        Switch swDynacache = new Switch(this);
        swDynacache.setChecked(preferences.getBoolean(PREF_DYNACACHE, true));
        swDynacache.setThumbTintList(new ColorStateList(
            new int[][]{{android.R.attr.state_checked}, {}}, new int[]{YELLOW, 0xFF8A8A8E}));
        swDynacache.setTrackTintList(new ColorStateList(
            new int[][]{{android.R.attr.state_checked}, {}}, new int[]{0x99FFC107, 0xFF3A3A3E}));
        swDynacache.setOnCheckedChangeListener((button, checked) ->
            preferences.edit().putBoolean(PREF_DYNACACHE, checked).apply());
        addRow(perf, getString(R.string.nfs_set_dynacache), swDynacache);

        // NFS_PERF: A06 60Hz ekran üçün stabil 60 default (limitsiz/limitsiz judder verir)
        int fps = preferences.getInt(PREF_FPS_LIMIT, 60);
        int fpsIndex = 0;
        for (int i = 0; i < FPS_VALUES.length; i++) if (FPS_VALUES[i] == fps) fpsIndex = i;
        addSpinnerRow(perf, getString(R.string.nfs_set_fps), getResources().getStringArray(R.array.nfs_fps_labels), fpsIndex, (position) ->
            preferences.edit().putInt(PREF_FPS_LIMIT, FPS_VALUES[position]).apply());

        // NFS_LAN: LAN / ZeroTier rejimi
        String savedLan = preferences.getString(PREF_LAN_MODE, LAN_MODE_VALUES[0]);
        int lanIndex = 0;
        for (int i = 0; i < LAN_MODE_VALUES.length; i++) if (LAN_MODE_VALUES[i].equals(savedLan)) lanIndex = i;
        addSpinnerRow(perf, getString(R.string.nfs_set_lan_mode), getResources().getStringArray(R.array.nfs_lan_labels), lanIndex, (position) -> {
            preferences.edit().putString(PREF_LAN_MODE, LAN_MODE_VALUES[position]).apply();
            if (!LAN_MODE_VALUES[position].equals("off"))
                Toast.makeText(this, R.string.nfs_lan_hint, Toast.LENGTH_LONG).show();
        });

        // GÖSTƏRİCİ
        LinearLayout hud = addCard(right, getString(R.string.nfs_set_hud_title));
        // NFS_PERF: FULL rejimi hər saniyə RAM/CPU/battery sorğusu edir → mikro-spike.
        // Default SIMPLE (yalnız FPS) qoyulur.
        addSpinnerRow(hud, getString(R.string.nfs_set_hud), getResources().getStringArray(R.array.nfs_hud_labels), preferences.getInt(PREF_HUD_MODE, 1), (position) ->
            preferences.edit().putInt(PREF_HUD_MODE, position).apply());

        // NFS_GAMEPAD: Bluetooth gamepad tanınması üçün model seçimi
        LinearLayout gamepadCard = addCard(right, getString(R.string.nfs_set_gamepad_title));
        ArrayList<GamepadHandler.GamepadModel> models = GamepadHandler.loadGamepadModels(this);
        String[] modelNames = new String[models.size()];
        String[] modelIds = new String[models.size()];
        int modelIndex = 0;
        String savedModel = preferences.getString("gamepad_model", "");
        for (int i = 0; i < models.size(); i++) {
            modelNames[i] = models.get(i).name;
            modelIds[i] = models.get(i).identifier();
            if (modelIds[i].equals(savedModel)) modelIndex = i;
        }
        addSpinnerRow(gamepadCard, getString(R.string.nfs_set_gamepad_model), modelNames, modelIndex, (position) -> {
            if (position == 0) preferences.edit().remove("gamepad_model").apply();
            else preferences.edit().putString("gamepad_model", modelIds[position]).apply();
        });

        // NFS MW 2005 DirectInput (STANDARD) gözləyir; bəzi pultlar XINPUT istəyir
        String[] mapperLabels = {"STANDARD (NFS MW üçün)", "XINPUT"};
        String[] mapperValues = {"0", "1"};
        String savedMapper = preferences.getString("dinputMapperType", "0");
        int mapperIndex = savedMapper.equals("1") ? 1 : 0;
        addSpinnerRow(gamepadCard, getString(R.string.nfs_set_gamepad_mapper), mapperLabels, mapperIndex, (position) ->
            preferences.edit().putString("dinputMapperType", mapperValues[position]).apply());

        // Oyunçu slotları (hansı pultun hansı oyunçu olması). Auto qalırsa boş saxlanılır.
        for (int i = 0; i < 4; i++) {
            final byte slot = (byte)i;
            TextView playerRow = new TextView(this);
            String cfg = preferences.getString("gamepad_player"+i, "");
            String label = cfg.isEmpty() ? "Auto" : new KeyValueSet(cfg).get("name", "Auto");
            playerRow.setText("Oyunçu " + (i+1) + ": " + label);
            playerRow.setTextSize(13);
            playerRow.setTextColor(0xFFDDDDDD);
            playerRow.setPadding(dp(10), dp(10), dp(10), dp(10));
            GradientDrawable rowBg = new GradientDrawable();
            rowBg.setColor(0xFF1E1E22);
            rowBg.setCornerRadius(dp(8));
            playerRow.setBackground(rowBg);
            playerRow.setTag(cfg);
            playerRow.setOnClickListener((v) -> {
                com.winlator.contentdialog.GamepadPlayerConfigDialog dialog =
                    new com.winlator.contentdialog.GamepadPlayerConfigDialog(v, slot);
                dialog.setOnDismissListener((dismissedDialog) -> {
                    String tag = v.getTag().toString();
                    if (tag.isEmpty()) preferences.edit().remove("gamepad_player"+slot).apply();
                    else preferences.edit().putString("gamepad_player"+slot, tag).apply();
                    playerRow.setText("Oyunçu " + (slot+1) + ": " +
                        (tag.isEmpty() ? "Auto" : new KeyValueSet(tag).get("name", "Auto")));
                });
                dialog.show();
            });
            gamepadCard.addView(playerRow, new LinearLayout.LayoutParams(match, wrap));
        }

        TextView note = new TextView(this);
        note.setText(R.string.nfs_set_note);
        note.setTextSize(12);
        note.setTextColor(MUTED);
        note.setPadding(0, 0, 0, dp(10));
        right.addView(note, new LinearLayout.LayoutParams(match, wrap));

        // Dəyişikliklər seçən kimi saxlanılır; düymə təsdiqləyib bağlayır.
        TextView save = new TextView(this);
        save.setText(R.string.nfs_set_save);
        save.setTextSize(16);
        save.setTypeface(Typeface.DEFAULT_BOLD);
        save.setLetterSpacing(0.1f);
        save.setTextColor(BG);
        save.setGravity(Gravity.CENTER);
        save.setBackgroundColor(YELLOW);
        save.setOnClickListener((v) -> {
            Toast.makeText(this, R.string.nfs_set_saved, Toast.LENGTH_SHORT).show();
            finish();
        });
        right.addView(save, new LinearLayout.LayoutParams(match, dp(52)));

        ScrollView scrollView = new ScrollView(this);
        scrollView.setFillViewport(true);
        scrollView.addView(body, new ViewGroup.LayoutParams(match, wrap));
        root.addView(scrollView, new LinearLayout.LayoutParams(match, 0, 1f));
        setContentView(root);
    }

    private void applyImmersive() {
        if (Build.VERSION.SDK_INT >= 28) {
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            getWindow().setAttributes(lp);
        }
        getWindow().getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
            View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) applyImmersive();
    }

    private interface OnPick { void onPick(int position); }

    private int dp(int value) {
        return (int)(value * getResources().getDisplayMetrics().density);
    }

    private LinearLayout addCard(LinearLayout column, String titleText) {
        final int match = ViewGroup.LayoutParams.MATCH_PARENT;
        final int wrap = ViewGroup.LayoutParams.WRAP_CONTENT;
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.HORIZONTAL);
        card.setBackgroundColor(CARD);
        View bar = new View(this);
        bar.setBackgroundColor(YELLOW);
        card.addView(bar, new LinearLayout.LayoutParams(dp(4), match));

        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(dp(16), dp(10), dp(16), dp(6));
        TextView title = new TextView(this);
        title.setText(titleText);
        title.setTextSize(15);
        title.setTextColor(0xFFFFFFFF);
        title.setLetterSpacing(0.08f);
        title.setPadding(0, 0, 0, dp(6));
        content.addView(title, new LinearLayout.LayoutParams(match, wrap));
        card.addView(content, new LinearLayout.LayoutParams(0, wrap, 1f));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(match, wrap);
        params.bottomMargin = dp(12);
        column.addView(card, params);
        return content;
    }

    private void addRow(LinearLayout content, String labelText, View control) {
        final int wrap = ViewGroup.LayoutParams.WRAP_CONTENT;
        View divider = new View(this);
        divider.setBackgroundColor(LINE);
        content.addView(divider, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(1)));

        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setMinimumHeight(dp(52));
        TextView label = new TextView(this);
        label.setText(labelText);
        label.setTextSize(14);
        label.setTextColor(MUTED);
        row.addView(label, new LinearLayout.LayoutParams(0, wrap, 1f));
        row.addView(control, new LinearLayout.LayoutParams(wrap, wrap));
        content.addView(row, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, wrap));
    }

    private ArrayAdapter<String> makeAdapter(String[] labels) {
        return new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, labels) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView view = (TextView)super.getView(position, convertView, parent);
                view.setText(getItem(position) + " \u25BE");
                view.setTextColor(0xFFFFFFFF);
                view.setTextSize(14);
                view.setSingleLine(true);
                view.setBackgroundColor(0x00000000);
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                TextView view = (TextView)super.getDropDownView(position, convertView, parent);
                view.setTextColor(0xFFFFFFFF);
                view.setTextSize(14);
                view.setBackgroundColor(0xFF17171A);
                return view;
            }
        };
    }

    private Spinner addSpinnerRow(LinearLayout content, String labelText, String[] labels, int selected, OnPick listener) {
        Spinner spinner = new Spinner(this);
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(BG);
        bg.setStroke(dp(1), LINE);
        spinner.setBackground(bg);
        spinner.setPopupBackgroundDrawable(new ColorDrawable(0xFF17171A));
        spinner.setPadding(dp(8), 0, dp(8), 0);
        spinner.setMinimumWidth(dp(120));
        spinner.setAdapter(makeAdapter(labels));
        spinner.setSelection(selected);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                listener.onPick(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        addRow(content, labelText, spinner);
        return spinner;
    }

    private void fillSizes(String aspect) {
        sizeValues.clear();
        sizeLabels.clear();
        String[] sizes = aspect.equals("5:4") ? SIZES_54 : SIZES_43;
        for (String size : sizes) {
            sizeValues.add(size);
            boolean heavy = size.equals("1280x960") || size.equals("1280x1024") || size.equals("1600x1200");
            String shown = size.replace('x', '\u00d7');
            sizeLabels.add(heavy ? shown + getString(R.string.nfs_set_heavy) : shown);
        }
    }

    private void applyAspect(String aspect) {
        String saved = preferences.getString(PREF_SCREEN_SIZE, DEFAULT_SCREEN_SIZE);
        fillSizes(aspect);

        int index = sizeValues.indexOf(saved);
        if (index < 0) index = 0;

        preferences.edit()
            .putString(PREF_ASPECT, aspect)
            .putString(PREF_SCREEN_SIZE, sizeValues.get(index))
            .apply();

        if (sizeSpinner != null) {
            sizeSpinner.setAdapter(makeAdapter(sizeLabels.toArray(new String[0])));
            sizeSpinner.setSelection(index);
        }
    }
}
