package com.winlator.inputcontrols;

// NFS_SCHEME: NFS MW 2005-in bütün idarə funksiyaları.
// Hər funksiyaya mövcud ikonlardan fərqli, aydın bir pictogram ikon verilir.
public class NfsControlScheme {
    public final String name;
    public final int iconId;
    public final String binding;
    public final String group;

    public NfsControlScheme(String name, int iconId, String binding, String group) {
        this.name = name;
        this.iconId = iconId;
        this.binding = binding;
        this.group = group;
    }

    public static final String G_DRIVE = "SÜRÜŞ";
    public static final String G_CAMERA = "KAMERA";
    public static final String G_MENU = "MENYU";

    // Manual gear: Shift Up = Shift, Shift Down = Ctrl, Speedbreaker = G, Nitro = X
    public static NfsControlScheme[] all() {
        return new NfsControlScheme[] {
            // SÜRÜŞ
            new NfsControlScheme("Qaz (Accelerate)", 31, "KEY_UP", G_DRIVE),
            new NfsControlScheme("Əyləc / Geri", 32, "KEY_DOWN", G_DRIVE),
            new NfsControlScheme("Sola dön", 33, "KEY_LEFT", G_DRIVE),
            new NfsControlScheme("Sağa dön", 34, "KEY_RIGHT", G_DRIVE),
            new NfsControlScheme("Əl əyləci (Handbrake)", 35, "KEY_SPACE", G_DRIVE),
            new NfsControlScheme("Speedbreaker", 36, "KEY_G", G_DRIVE),
            new NfsControlScheme("Nitro (N2O)", 37, "KEY_X", G_DRIVE),
            new NfsControlScheme("Gear ↑ (Shift Up)", 38, "KEY_SHIFT_L", G_DRIVE),
            new NfsControlScheme("Gear ↓ (Shift Down)", 39, "KEY_CTRL_L", G_DRIVE),
            // KAMERA
            new NfsControlScheme("Kameranı dəyiş", 40, "KEY_C", G_CAMERA),
            new NfsControlScheme("Geri bax", 41, "KEY_L", G_CAMERA),
            new NfsControlScheme("Kameranı geri çək", 42, "KEY_P", G_CAMERA),
            // MENYU / DIGƏR
            new NfsControlScheme("Maşını sıfırla", 43, "KEY_R", G_MENU),
            new NfsControlScheme("Hadisəni başlat", 44, "KEY_ENTER", G_MENU),
            new NfsControlScheme("Qara siyahı", 45, "KEY_B", G_MENU),
            new NfsControlScheme("Dünya xəritəsi", 46, "KEY_M", G_MENU),
            new NfsControlScheme("SMS mesajları", 47, "KEY_TAB", G_MENU),
            new NfsControlScheme("Növbəti mahnı", 48, "KEY_T", G_MENU),
            new NfsControlScheme("Pauza", 49, "KEY_ESC", G_MENU),
        };
    }
}
