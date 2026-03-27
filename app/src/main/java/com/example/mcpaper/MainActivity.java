package com.example.mcpaper;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.mcpaper.databinding.ActivityMainBinding;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_EDIT_FILE = 2001;

    private ActivityMainBinding binding;
    private File currentDirectory;
    private File rootDirectory;
    private FileListAdapter adapter;

    private final ActivityResultLauncher<String> importFileLauncher =
            registerForActivityResult(new ActivityResultContracts.OpenDocument(), this::copyImportedFile);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupDirectory();
        setupToolbar();
        setupList();
        loadFiles();
    }

    private void setupDirectory() {
        File base = new File(getExternalFilesDir(null), "home/mcpaper");
        if (!base.exists() && !base.mkdirs()) {
            Toast.makeText(this, R.string.create_root_failed, Toast.LENGTH_LONG).show();
        }
        rootDirectory = base;
        currentDirectory = base;
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        binding.toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_import) {
                importFileLauncher.launch("*/*");
                return true;
            }
            return false;
        });
    }

    private void setupList() {
        adapter = new FileListAdapter(this::onFileSelected);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);

        binding.fabImport.setOnClickListener(v -> importFileLauncher.launch("*/*"));
    }

    private void onFileSelected(File file) {
        if (file.isDirectory()) {
            currentDirectory = file;
            loadFiles();
            return;
        }

        Intent intent = new Intent(this, EditorActivity.class);
        intent.putExtra(EditorActivity.EXTRA_FILE_PATH, file.getAbsolutePath());
        startActivityForResult(intent, REQUEST_EDIT_FILE);
    }

    private void loadFiles() {
        binding.tvCurrentPath.setText(getString(R.string.current_path, currentDirectory.getAbsolutePath()));

        File[] list = currentDirectory.listFiles();
        List<File> files = new ArrayList<>();
        if (list != null) {
            files.addAll(Arrays.asList(list));
            files.sort(Comparator
                    .comparing(File::isFile)
                    .thenComparing(file -> file.getName().toLowerCase()));
        }

        adapter.submitList(files);
        binding.emptyView.setVisibility(files.isEmpty() ? android.view.View.VISIBLE : android.view.View.GONE);
        binding.emptyView.setText(R.string.empty_directory);
    }

    private void copyImportedFile(Uri uri) {
        if (uri == null) {
            return;
        }

        String fileName = FileUtils.resolveFileName(this, uri);
        if (fileName == null || fileName.trim().isEmpty()) {
            fileName = "imported_" + System.currentTimeMillis();
        }

        File dest = new File(currentDirectory, fileName);
        try (InputStream in = getContentResolver().openInputStream(uri);
             FileOutputStream out = new FileOutputStream(dest)) {
            if (in == null) {
                throw new IOException("Cannot read source file");
            }
            byte[] buffer = new byte[8192];
            int len;
            while ((len = in.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
            Toast.makeText(this, getString(R.string.import_success, fileName), Toast.LENGTH_SHORT).show();
            loadFiles();
        } catch (IOException e) {
            Toast.makeText(this, getString(R.string.import_failed, e.getMessage()), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        if (currentDirectory != null && !currentDirectory.equals(rootDirectory)) {
            currentDirectory = currentDirectory.getParentFile();
            loadFiles();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_EDIT_FILE) {
            loadFiles();
        }
    }
}
