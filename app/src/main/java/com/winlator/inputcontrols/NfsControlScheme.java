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
            // SÜRÜŞ — yeni, fərqli pictogram ikonlar (31..39)
            new NfsControlScheme("Qaz (Accelerate)", 31, "KEY_UP"),          // pedal
            new NfsControlScheme("Əyləc / Geri", 32, "KEY_DOWN"),            // stop
            new NfsControlScheme("Sola dön", 33, "KEY_LEFT"),                // sükan ◀
            new NfsControlScheme("Sağa dön", 34, "KEY_RIGHT"),               // sükan ▶
            new NfsControlScheme("Əl əyləci (Handbrake)", 35, "KEY_SPACE"),  // əl əyləci
            new NfsControlScheme("Speedbreaker", 36, "KEY_G"),               // zəncir/ildırım
            new NfsControlScheme("Nitro (N2O)", 37, "KEY_X"),                // alov
            new NfsControlScheme("Gear ↑ (Shift Up)", 38, "KEY_SHIFT_L"),    // dişli ↑
            new NfsControlScheme("Gear ↓ (Shift Down)", 39, "KEY_CTRL_L"),   // dişli ↓
            // KAMERA (40..42)
            new NfsControlScheme("Kameranı dəyiş", 40, "KEY_C"),             // kamera
            new NfsControlScheme("Geri bax", 41, "KEY_L"),                   // güzgü
            new NfsControlScheme("Kameranı geri çək", 42, "KEY_P"),          // geri ox
            // MENYU / DIGƏR (43..49)
            new NfsControlScheme("Maşını sıfırla", 43, "KEY_R"),             // circular arrow
            new NfsControlScheme("Hadisəni başlat", 44, "KEY_ENTER"),        // bayraq
            new NfsControlScheme("Qara siyahı", 45, "KEY_B"),                // siyahı
            new NfsControlScheme("Dünya xəritəsi", 46, "KEY_M"),             // qlobus
            new NfsControlScheme("SMS mesajları", 47, "KEY_TAB"),            // SMS
            new NfsControlScheme("Növbəti mahnı", 48, "KEY_T"),              // musiqi
            new NfsControlScheme("Pauza", 49, "KEY_ESC"),                    // pauza
        };
    }
}
