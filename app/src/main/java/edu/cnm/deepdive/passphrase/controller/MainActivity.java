package edu.cnm.deepdive.passphrase.controller;

import android.content.Intent;
import android.transition.Slide;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.core.app.ActivityOptionsCompat;
import androidx.lifecycle.ViewModelProvider;
import dagger.hilt.android.AndroidEntryPoint;
import edu.cnm.deepdive.passphrase.R;
import edu.cnm.deepdive.passphrase.databinding.ActivityMainBinding;
import edu.cnm.deepdive.passphrase.model.Passphrase;
import edu.cnm.deepdive.passphrase.service.PassphraseServiceProxy;
import edu.cnm.deepdive.passphrase.viewmodel.LoginViewModel;
import io.reactivex.rxjava3.schedulers.Schedulers;
import javax.inject.Inject;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

  private ActivityMainBinding binding;
  private LoginViewModel viewModel;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
    getWindow().setEnterTransition(new Slide(Gravity.START)); //add effect to windows
    getWindow().setExitTransition(new Slide(Gravity.START));
    binding = ActivityMainBinding.inflate(getLayoutInflater());
    setContentView(binding.getRoot());
    viewModel = new ViewModelProvider(this)
        .get(LoginViewModel.class);
    viewModel.getAccount()
        .observe(this, (account) -> {
          if(account != null){
            binding.name.setText(account.getDisplayName());
          }else {
            Intent intent = new Intent(this, LoginActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            //noinspection unchecked
            startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(this).toBundle());
          }
        });
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    super.onCreateOptionsMenu(menu);
    getMenuInflater().inflate(R.menu.main_options, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    boolean handled;
    if(item.getItemId() == R.id.sign_out){
      viewModel.signOut();
      handled = true;
    }else {
      handled = super.onOptionsItemSelected(item);
    }
    return handled;
  }
}