package com.example.mcpaper;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mcpaper.databinding.ActivityEditorBinding;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class EditorActivity extends AppCompatActivity {

    public static final String EXTRA_FILE_PATH = "extra_file_path";

    private ActivityEditorBinding binding;
    private File editingFile;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String path = getIntent().getStringExtra(EXTRA_FILE_PATH);
        if (path == null) {
            Toast.makeText(this, R.string.no_file_selected, Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        editingFile = new File(path);
        binding.toolbar.setTitle(editingFile.getName());
        binding.toolbar.setNavigationOnClickListener(v -> finish());
        binding.btnSave.setOnClickListener(v -> saveContent());

        loadContent();
    }

    private void loadContent() {
        try {
            String content = Files.readString(editingFile.toPath(), StandardCharsets.UTF_8);
            binding.editor.setText(content);
        } catch (IOException e) {
            Toast.makeText(this, getString(R.string.read_failed, e.getMessage()), Toast.LENGTH_LONG).show();
        }
    }

    private void saveContent() {
        try {
            Files.writeString(editingFile.toPath(), binding.editor.getText().toString(), StandardCharsets.UTF_8);
            Toast.makeText(this, R.string.save_success, Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
        } catch (IOException e) {
            Toast.makeText(this, getString(R.string.save_failed, e.getMessage()), Toast.LENGTH_LONG).show();
        }
    }
}
