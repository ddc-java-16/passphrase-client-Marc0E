package edu.cnm.deepdive.passphrase.controller;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import edu.cnm.deepdive.passphrase.adapter.PassphrasesAdapter;
import edu.cnm.deepdive.passphrase.databinding.FragmentPassphrasesBinding;
import edu.cnm.deepdive.passphrase.viewmodel.PassphraseViewModel;
import org.jetbrains.annotations.NotNull;


public class PassphrasesFragment extends Fragment {

  private FragmentPassphrasesBinding binding;
  private PassphraseViewModel viewModel;

  @Nullable
  @org.jetbrains.annotations.Nullable
  @Override
  public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container,
      @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
    binding = FragmentPassphrasesBinding.inflate(inflater, container, false);
    binding.refresh.setOnClickListener((v) -> viewModel.fetch());
    //noinspection DataFlowIssue
    binding.search.setOnClickListener((v) -> viewModel.search(binding.searchText.getText().toString()));
    binding.create.setOnClickListener((v) -> openDialog(null));
    // TODO: 11/2/23 Attach listeners.
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    viewModel = new ViewModelProvider(requireActivity())
        .get(PassphraseViewModel.class);
    getLifecycle().addObserver(viewModel);
    viewModel
        .getPassphrases()
        .observe(getViewLifecycleOwner(), (passphrases) -> {
          binding.passphrases.setAdapter(new PassphrasesAdapter(requireContext(), passphrases,
              (v, pos, passphrase) -> openDialog(passphrase.getKey()),
              (v, pos, passphrase) -> Log.d(getClass().getSimpleName(),passphrase+" long-clicked")));
        });
  }

  private void openDialog(String key) {
    Navigation.findNavController(binding.getRoot())
        .navigate(PassphrasesFragmentDirections.openEditPassphraseFragment().setKey(key));
  }
}
