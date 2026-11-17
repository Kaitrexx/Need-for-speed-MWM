package com.winlator.inputcontrols;

// NFS_SCHEME: NFS MW 2005-in bütün idarə funksiyaları.
// Hər funksiyaya mövcud ikonlardan (inputcontrols/icons/{id}.png) fərqli,
// görünən bir ikon verilir. MƏTN YOX — düymənin üzərində ikon olur.
// Mövcud (boş olmayan) ikonlar: 0..17, 20, 22.
public class NfsControlScheme {
    public final String name;
    public final int iconId;
    public final String binding;

    public NfsControlScheme(String name, int iconId, String binding) {
        this.name = name;
        this.iconId = iconId;
        this.binding = binding;
    }

    // Manual gear: Shift Up = Shift, Shift Down = Ctrl, Speedbreaker = G, Nitro = X
    public static NfsControlScheme[] all() {
        return new NfsControlScheme[] {
            // SÜRÜŞ — ox üçbucaqları (hamısı fərqli və anlaşılan)
            new NfsControlScheme("Qaz (Accelerate)", 7, "KEY_UP"),          // ▲
            new NfsControlScheme("Əyləc / Geri", 9, "KEY_DOWN"),            // ▼
            new NfsControlScheme("Sola dön", 6, "KEY_LEFT"),                // ◀
            new NfsControlScheme("Sağa dön", 8, "KEY_RIGHT"),               // ▶
            new NfsControlScheme("Əl əyləci (Handbrake)", 5, "KEY_SPACE"),  // ●
            new NfsControlScheme("Speedbreaker", 4, "KEY_G"),               // ↓
            new NfsControlScheme("Nitro (N2O)", 3, "KEY_X"),                // ↑
            new NfsControlScheme("Gear ↑ (Shift Up)", 1, "KEY_SHIFT_L"),    // ←
            new NfsControlScheme("Gear ↓ (Shift Down)", 2, "KEY_CTRL_L"),   // ↔  (düzgün binding)
            // KAMERA
            new NfsControlScheme("Kameranı dəyiş", 20, "KEY_C"),            // ☰
            new NfsControlScheme("Geri bax", 16, "KEY_L"),                  // ／
            new NfsControlScheme("Kameranı geri çək", 17, "KEY_P"),         // ＼
            // MENYU / DIGƏR
            new NfsControlScheme("Maşını sıfırla", 14, "KEY_R"),            // ▣
            new NfsControlScheme("Hadisəni başlat", 13, "KEY_ENTER"),       // ≡
            new NfsControlScheme("Qara siyahı", 22, "KEY_B"),               // ▤
            new NfsControlScheme("Dünya xəritəsi", 10, "KEY_M"),            // mouse
            new NfsControlScheme("SMS mesajları", 11, "KEY_TAB"),           // mouse-click
            new NfsControlScheme("Növbəti mahnı", 12, "KEY_T"),             // mouse
            new NfsControlScheme("Pauza", 15, "KEY_ESC"),                   // ✕
        };
    }
}
