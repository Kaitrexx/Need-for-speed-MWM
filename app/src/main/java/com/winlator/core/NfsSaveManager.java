package com.winlator.core;

import android.content.Context;
import android.widget.Toast;

import androidx.preference.PreferenceManager;

import com.winlator.R;
import com.winlator.container.Container;
import com.winlator.container.ContainerManager;

import java.io.File;

// NFS_SAVE: NFS MW save-larını oyun qovluğundakı SAVE/ papkasında saxlayır.
// Proqram silinib yenidən quraşdırılanda save-lar qovluqda qaldığı üçün
// oyun yeni profildən başlamır.
public abstract class NfsSaveManager {
    public static final String SAVE_FOLDER = "SAVE";

    public static File getBackupDir(Context context) {
        String dir = PreferenceManager.getDefaultSharedPreferences(context).getString("nfs_game_dir", null);
        if (dir == null) return null;
        File folder = new File(dir, SAVE_FOLDER);
        if (!folder.isDirectory() && !folder.mkdirs()) return null;
        return folder;
    }

    public static File getWineSaveDir(Container container) {
        File user = container.getUserDir();
        File modern = new File(user, "Documents/NFS Most Wanted");
        if (modern.isDirectory()) return modern;
        return new File(user, "My Documents/NFS Most Wanted");
    }

    private static void copyTree(File src, File dst) {
        if (src == null || !src.isDirectory()) return;
        File[] children = src.listFiles();
        if (children == null || children.length == 0) return;
        dst.mkdirs();
        for (File file : children) {
            File target = new File(dst, file.getName());
            if (file.isDirectory()) copyTree(file, target);
            else FileUtils.copy(file, target);
        }
    }

    // Wine save-lərindən SAVE/ papkasına yedəklə
    public static boolean backup(Context context, int containerId) {
        File backup = getBackupDir(context);
        if (backup == null) return false;
        Container container = new ContainerManager(context).getContainerById(containerId);
        if (container == null) return false;
        File wine = getWineSaveDir(container);
        if (!wine.isDirectory()) return false;
        File[] files = wine.listFiles();
        if (files == null || files.length == 0) return false;
        copyTree(wine, backup);
        return true;
    }

    // SAVE/ papkasından Wine save-lərinə bərpa et
    public static boolean restore(Context context, int containerId) {
        File backup = getBackupDir(context);
        if (backup == null) return false;
        Container container = new ContainerManager(context).getContainerById(containerId);
        if (container == null) return false;
        File[] files = backup.listFiles();
        if (files == null || files.length == 0) return false;
        copyTree(backup, getWineSaveDir(container));
        return true;
    }

    public static void backupWithToast(Context context, int containerId) {
        boolean ok = backup(context, containerId);
        Toast.makeText(context, ok ? R.string.nfs_save_done : R.string.nfs_save_none,
                Toast.LENGTH_SHORT).show();
    }
}
