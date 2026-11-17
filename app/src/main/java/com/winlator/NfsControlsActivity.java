package com.winlator;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.winlator.core.AppUtils;
import com.winlator.core.LocaleHelper;
import com.winlator.inputcontrols.ControlsProfile;
import com.winlator.inputcontrols.InputControlsManager;

import java.util.ArrayList;

// NFS_DESIGN_V3: Winlator-ın stock "İdarəetmə" ekranı əvəzinə NFS MW üslubunda ekran.
public class NfsControlsActivity extends AppCompatActivity {
    private static final int YELLOW = 0xFFFFC107;
    private static final int BG = 0xFF0B0B0D;
    private static final int CARD = 0xFF131315;
    private static final int LINE = 0xFF2A2A2E;
    private static final int MUTED = 0xFFB4B4B4;

    private InputControlsManager manager;
    private LinearLayout profilesBox;
    private ControlsProfile selected;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.setSystemLocale(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppUtils.setActivityTheme(this);
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        getWindow().setStatusBarColor(BG);
        getWindow().setNavigationBarColor(BG);
        getWindow().setFlags(android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN,
                android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN);
        manager = new InputControlsManager(this);
        buildUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        renderProfiles();
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }

    private GradientDrawable background(int color) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color);
        d.setCornerRadius(dp(12));
        d.setStroke(1, LINE);
        return d;
    }

    private TextView actionButton(String text, int color, View.OnClickListener listener) {
        TextView view = new TextView(this);
        view.setText(text);
        view.setTextSize(14);
        view.setTypeface(Typeface.DEFAULT_BOLD);
        view.setGravity(Gravity.CENTER);
        view.setTextColor(color == YELLOW ? BG : YELLOW);
        view.setBackground(background(color));
        view.setPadding(dp(16), dp(9), dp(16), dp(9));
        view.setOnClickListener(listener);
        return view;
    }

    private LinearLayout card(String title) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setBackground(background(CARD));
        card.setPadding(dp(16), dp(12), dp(16), dp(12));

        TextView label = new TextView(this);
        label.setText(title);
        label.setTextSize(12);
        label.setTypeface(Typeface.DEFAULT_BOLD);
        label.setTextColor(YELLOW);
        label.setLetterSpacing(0.15f);
        card.addView(label);
        return card;
    }

    private void buildUI() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(BG);

        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setBackgroundColor(0xFF141416);
        header.setPadding(dp(24), dp(10), dp(24), dp(10));

        TextView back = new TextView(this);
        back.setText("\u2039");
        back.setTextSize(30);
        back.setTextColor(YELLOW);
        back.setPadding(0, 0, dp(20), 0);
        back.setOnClickListener(v -> finish());

        TextView title = new TextView(this);
        title.setText(R.string.nfs_controls_title);
        title.setTextSize(17);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setTextColor(0xFFFFFFFF);

        header.addView(back);
        header.addView(title);
        root.addView(header, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        ScrollView scroll = new ScrollView(this);
        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(dp(24), dp(18), dp(24), dp(24));

        LinearLayout profilesCard = card(getString(R.string.nfs_controls_profiles));
        profilesBox = new LinearLayout(this);
        profilesBox.setOrientation(LinearLayout.VERTICAL);
        profilesCard.addView(profilesBox, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        content.addView(profilesCard, cardParams());

        LinearLayout actions = card(getString(R.string.nfs_controls_actions));
        TextView edit = actionButton(getString(R.string.nfs_controls_edit), YELLOW, v -> {
            if (selected == null) {
                Toast.makeText(this, R.string.nfs_controls_no_profile, Toast.LENGTH_SHORT).show();
                return;
            }
            openEditor(selected);
        });
        TextView create = actionButton(getString(R.string.nfs_controls_new), 0xFF232326, v -> {
            ControlsProfile profile = manager.createProfile("NFS Profile");
            profile.save();
            renderProfiles();
            openEditor(profile);
        });
        LinearLayout buttons = new LinearLayout(this);
        buttons.setOrientation(LinearLayout.HORIZONTAL);
        buttons.addView(edit, new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        LinearLayout.LayoutParams gap = new LinearLayout.LayoutParams(dp(10),
                LinearLayout.LayoutParams.WRAP_CONTENT);
        buttons.addView(create, gap);
        actions.addView(buttons, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        content.addView(actions, cardParams());

        TextView note = new TextView(this);
        note.setText(R.string.nfs_controls_note);
        note.setTextSize(12);
        note.setTextColor(MUTED);
        note.setPadding(0, dp(10), 0, 0);
        content.addView(note);

        scroll.addView(content, new ScrollView.LayoutParams(
                ScrollView.LayoutParams.MATCH_PARENT, ScrollView.LayoutParams.WRAP_CONTENT));
        root.addView(scroll, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f));
        setContentView(root);
    }

    private LinearLayout.LayoutParams cardParams() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.bottomMargin = dp(12);
        return params;
    }

    private void renderProfiles() {
        profilesBox.removeAllViews();
        ArrayList<ControlsProfile> profiles = manager.getProfiles(true);
        if (selected != null) {
            for (ControlsProfile profile : profiles) {
                if (profile.id == selected.id) {
                    selected = profile;
                    break;
                }
            }
        }
        if (selected == null && !profiles.isEmpty()) selected = profiles.get(0);

        if (profiles.isEmpty()) {
            TextView empty = new TextView(this);
            empty.setText(R.string.nfs_controls_empty);
            empty.setTextColor(MUTED);
            empty.setTextSize(13);
            profilesBox.addView(empty);
            return;
        }

        for (ControlsProfile profile : profiles) {
            TextView row = new TextView(this);
            row.setText(profile.getName());
            row.setTextSize(14);
            row.setTextColor(profile == selected ? YELLOW : 0xFFDDDDDD);
            row.setPadding(dp(10), dp(9), dp(10), dp(9));
            if (profile == selected) row.setBackground(background(0xFF1E1E22));
            row.setOnClickListener(v -> {
                selected = profile;
                renderProfiles();
            });
            profilesBox.addView(row);
        }
    }

    private void openEditor(ControlsProfile profile) {
        Intent intent = new Intent(this, ControlsEditorActivity.class);
        intent.putExtra("profile_id", profile.id);
        startActivity(intent);
    }
}
