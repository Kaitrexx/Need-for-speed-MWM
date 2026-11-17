package com.winlator.inputcontrols;

import android.content.Context;

// NFS_SCHEME: NFS MW 2005-in bütün idarə funksiyaları və onların default
// klaviatura bağlamaları (oyunun Options → Controls ekranından götürülüb).
// Bu kataloqdan istifadəçi istədiyi düyməni profilə əlavə edə bilər.
public class NfsControlScheme {
    public final String name;      // istifadəçiyə görünən ad
    public final String binding;   // Binding enum adı
    public final int iconId;       // assets/inputcontrols/icons/{id}.png
    public final float x;          // ekrandakı default yer (0..1)
    public final float y;
    public final String group;     // kateqoriya

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

    // Manual gear üçün konfliktsiz sxem: Shift Up = Shift, Shift Down = Control,
    // Speedbreaker = G (ikinci bağlama), Nitro = X / Meta.
    public static NfsControlScheme[] all() {
        return new NfsControlScheme[] {
            // SÜRÜŞ
            new NfsControlScheme("Qaz (Accelerate)", "KEY_UP", 22, 0.91f, 0.64f, GROUP_DRIVE),
            new NfsControlScheme("Əyləc / Geri", "KEY_DOWN", 23, 0.76f, 0.82f, GROUP_DRIVE),
            new NfsControlScheme("Sola dön", "KEY_LEFT", 20, 0.08f, 0.78f, GROUP_DRIVE),
            new NfsControlScheme("Sağa dön", "KEY_RIGHT", 21, 0.24f, 0.78f, GROUP_DRIVE),
            new NfsControlScheme("Əl əyləci (Handbrake)", "KEY_SPACE", 26, 0.58f, 0.86f, GROUP_DRIVE),
            new NfsControlScheme("Speedbreaker", "KEY_G", 25, 0.76f, 0.52f, GROUP_DRIVE),
            new NfsControlScheme("Nitro (N2O)", "KEY_X", 24, 0.91f, 0.36f, GROUP_DRIVE),
            new NfsControlScheme("Gear ↑ (Shift Up)", "KEY_SHIFT_L", 28, 0.66f, 0.52f, GROUP_DRIVE),
            new NfsControlScheme("Gear ↓ (Shift Down)", "KEY_CTRL_L", 27, 0.66f, 0.68f, GROUP_DRIVE),
            // KAMERA
            new NfsControlScheme("Kameranı dəyiş", "KEY_C", 28, 0.50f, 0.09f, GROUP_CAMERA),
            new NfsControlScheme("Geri bax", "KEY_L", 29, 0.60f, 0.09f, GROUP_CAMERA),
            new NfsControlScheme("Kameranı geri çək", "KEY_P", 30, 0.70f, 0.09f, GROUP_CAMERA),
            // MENYU / DIGƏR
            new NfsControlScheme("Maşını sıfırla", "KEY_R", 27, 0.40f, 0.24f, GROUP_MENU),
            new NfsControlScheme("Hadisəni başlat", "KEY_ENTER", 26, 0.40f, 0.36f, GROUP_MENU),
            new NfsControlScheme("Qara siyahı", "KEY_B", 28, 0.40f, 0.48f, GROUP_MENU),
            new NfsControlScheme("Dünya xəritəsi", "KEY_M", 29, 0.40f, 0.60f, GROUP_MENU),
            new NfsControlScheme("SMS mesajları", "KEY_TAB", 30, 0.40f, 0.72f, GROUP_MENU),
            new NfsControlScheme("Növbəti mahnı", "KEY_T", 26, 0.40f, 0.84f, GROUP_MENU),
            new NfsControlScheme("Pauza", "KEY_ESC", 27, 0.30f, 0.09f, GROUP_MENU),
        };
    }
}
