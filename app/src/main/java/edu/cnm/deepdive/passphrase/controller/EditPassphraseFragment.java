package edu.cnm.deepdive.passphrase.controller;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AlertDialog.Builder;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import edu.cnm.deepdive.passphrase.R;
import edu.cnm.deepdive.passphrase.databinding.FragmentEditPassphraseBinding;
import org.jetbrains.annotations.NotNull;

public class EditPassphraseFragment extends DialogFragment implements TextWatcher {

  private FragmentEditPassphraseBinding binding;
  private String key;
  private AlertDialog dialog;

  @NonNull
  @Override
  public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
    binding = FragmentEditPassphraseBinding.inflate(getLayoutInflater(), null, false);
    key = EditPassphraseFragmentArgs.fromBundle(getArguments()).getKey();
    dialog = new Builder(requireContext())
        //.setIcon() // TODO: 11/3/23 Import a suitable icon drawable.
        .setTitle(R.string.passphrase_details)
        .setView(binding.getRoot())
        .setPositiveButton(android.R.string.ok, (dlg, which) -> {/* TODO Handle click on OK.*/})
        .setNegativeButton(android.R.string.cancel, (dlg, which) -> {/*Do nothing.*/})
        .create();
    dialog.setOnShowListener((dlg) -> checkSubmitConditions());
    binding.name.addTextChangedListener(this);
    binding.words.addTextChangedListener(this);
    binding.generate.setOnClickListener((v) -> {/* TODO Request generation of random passphrase.*/});
    binding.length.setMinValue(2); // FIXME: 11/3/23 Read from resources.
    binding.length.setMaxValue(10);// FIXME: 11/3/23 Read from resources.
    return dialog;

  }

  @Nullable
  @Override
  public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return super.onCreateView(inflater, container, savedInstanceState);
  }

  @Override
  public void onViewCreated(@NonNull @NotNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    // TODO: 11/3/23 Attach to view model & observe.
  }

  @Override
  public void onDestroyView() {
    binding = null;
    super.onDestroyView();
  }

  @Override
  public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    // Do nothing.
  }

  @Override
  public void onTextChanged(CharSequence s, int start, int before, int count) {
  // Do nothing.
  }

  @Override
  public void afterTextChanged(Editable s) {
    checkSubmitConditions();
  }

  private void checkSubmitConditions() {
    // TODO: 11/3/23 Enable/disable OK button, based on content of binding.words and binding.name.
  }
}
