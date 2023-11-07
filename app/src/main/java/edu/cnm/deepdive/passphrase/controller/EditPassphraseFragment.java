package edu.cnm.deepdive.passphrase.controller;

import android.app.Dialog;
import android.content.DialogInterface;
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
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import edu.cnm.deepdive.passphrase.R;
import edu.cnm.deepdive.passphrase.databinding.FragmentEditPassphraseBinding;
import edu.cnm.deepdive.passphrase.model.Passphrase;
import edu.cnm.deepdive.passphrase.viewmodel.PassphraseViewModel;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;

public class EditPassphraseFragment extends DialogFragment implements TextWatcher {

  private static final Pattern SPLITTER = Pattern.compile("\\W+");
  private FragmentEditPassphraseBinding binding;
  private String key;
  private AlertDialog dialog;
  private PassphraseViewModel viewModel;
  private Passphrase passphrase;

  @NonNull
  @Override
  public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
    binding = FragmentEditPassphraseBinding.inflate(getLayoutInflater(), null, false);
    key = EditPassphraseFragmentArgs.fromBundle(getArguments()).getKey();
    dialog = new Builder(requireContext())
        //.setIcon() // TODO: 11/3/23 Import a suitable icon drawable.
        .setTitle(R.string.passphrase_details)
        .setView(binding.getRoot())
        .setPositiveButton(android.R.string.ok, (dlg, which) -> save())
        .setNegativeButton(android.R.string.cancel, (dlg, which) -> {/*Do nothing.*/})
        .create();
    dialog.setOnShowListener((dlg) -> checkSubmitConditions());
    binding.name.addTextChangedListener(this);
    binding.words.addTextChangedListener(this);
    binding.generate.setOnClickListener((v) -> viewModel.generate(binding.length.getValue()));
    binding.length.setMinValue(2); // FIXME: 11/3/23 Read from resources.
    binding.length.setMaxValue(10);// FIXME: 11/3/23 Read from resources.
    return dialog;

  }

  @Nullable
  @Override
  public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull @NotNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    viewModel = new ViewModelProvider(requireActivity())
        .get(PassphraseViewModel.class);
    viewModel.clearGenerated();
    viewModel
        .getGenerated()
        .observe(getViewLifecycleOwner(),
            (words) -> binding.words.setText((words != null) ? String.join("\n", words) : ""));
    passphrase = new Passphrase();
    viewModel.clearPassphrase();
    if(key != null){
      viewModel
          .getPassphrase()
          .observe(getViewLifecycleOwner(), passphrase -> {
            if(passphrase != null){
              binding.name.setText(passphrase.getName());
              binding.words.setText(String.join("\n",passphrase.getWords()));
              this.passphrase = passphrase;
            }
          });
      viewModel.fetch(key);
    }
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

private void save(){
  //noinspection DataFlowIssue
  passphrase.setName(binding.name.getText().toString().strip());
  //passphrase.getWords().addAll(
  // noinspection DataFlowIssue
        SPLITTER
            .splitAsStream(binding.words.getText().toString())
            .filter((word) -> !word.isEmpty())
            .forEach((word) -> passphrase.append(word));
            //.collect(Collectors.toList()) this is another option to replaces the line forEach.
    viewModel.save(passphrase);
  // TODO: 11/6/23 Send passphrase to service.
}
  private void checkSubmitConditions() {
    //noinspection DataFlowIssue
    dialog.getButton(DialogInterface.BUTTON_POSITIVE)
        .setEnabled(
            !binding.name.getText().toString().isBlank()
            && !binding.words.getText().toString().isBlank()
        );
  }
}
