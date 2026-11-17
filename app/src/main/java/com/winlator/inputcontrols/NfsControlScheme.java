package com.winlator.inputcontrols;

// NFS_SCHEME: NFS MW 2005-in bütün idarə funksiyaları.
// NFS_FIX: anlaşılmayan ikonlar əvəzinə qısa, oxunula bilən MƏTN (label) istifadə
// olunur — ikonId = 0 qoyulur ki, ControlElement text-i çəksin.
public class NfsControlScheme {
    public final String name;      // siyahıda görünən tam ad
    public final String label;     // düymənin üzərində yazılacaq qısa mətn
    public final String binding;   // Binding enum adı

    public NfsControlScheme(String name, String label, String binding) {
        this.name = name;
        this.label = label;
        this.binding = binding;
    }

    // Manual gear: Shift Up = Shift, Shift Down = Ctrl, Speedbreaker = G, Nitro = X
    public static NfsControlScheme[] all() {
        return new NfsControlScheme[] {
            // SÜRÜŞ
            new NfsControlScheme("Qaz (Accelerate)", "GAS", "KEY_UP"),
            new NfsControlScheme("Əyləc / Geri", "BRAKE", "KEY_DOWN"),
            new NfsControlScheme("Sola dön", "◀ SOL", "KEY_LEFT"),
            new NfsControlScheme("Sağa dön", "SAĞ ▶", "KEY_RIGHT"),
            new NfsControlScheme("Əl əyləci (Handbrake)", "HANDBRAKE", "KEY_SPACE"),
            new NfsControlScheme("Speedbreaker", "SPEED", "KEY_G"),
            new NfsControlScheme("Nitro (N2O)", "N2O", "KEY_X"),
            new NfsControlScheme("Gear ↑ (Shift Up)", "GEAR▲", "KEY_SHIFT_L"),
            new NfsControlScheme("Gear ↓ (Shift Down)", "GEAR▼", "KEY_CTRL_L"),
            // KAMERA
            new NfsControlScheme("Kameranı dəyiş", "CAM", "KEY_C"),
            new NfsControlScheme("Geri bax", "BACK", "KEY_L"),
            new NfsControlScheme("Kameranı geri çək", "PULL", "KEY_P"),
            // MENYU / DIGƏR
            new NfsControlScheme("Maşını sıfırla", "RESET", "KEY_R"),
            new NfsControlScheme("Hadisəni başlat", "EVENT", "KEY_ENTER"),
            new NfsControlScheme("Qara siyahı", "LIST", "KEY_B"),
            new NfsControlScheme("Dünya xəritəsi", "MAP", "KEY_M"),
            new NfsControlScheme("SMS mesajları", "SMS", "KEY_TAB"),
            new NfsControlScheme("Növbəti mahnı", "MUSIC", "KEY_T"),
            new NfsControlScheme("Pauza", "PAUSE", "KEY_ESC"),
        };
    }
}
