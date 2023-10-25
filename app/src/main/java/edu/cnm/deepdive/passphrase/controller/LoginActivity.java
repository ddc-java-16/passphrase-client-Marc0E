package edu.cnm.deepdive.passphrase.controller;

import android.content.Intent;
import android.transition.Slide;
import android.view.Gravity;
import android.view.Window;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.core.app.ActivityOptionsCompat;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.snackbar.Snackbar;
import dagger.hilt.android.AndroidEntryPoint;
import edu.cnm.deepdive.passphrase.R;
import edu.cnm.deepdive.passphrase.databinding.ActivityLoginBinding;
import edu.cnm.deepdive.passphrase.viewmodel.LoginViewModel;

@AndroidEntryPoint
public class LoginActivity extends AppCompatActivity {

  private ActivityLoginBinding binding;
  private LoginViewModel viewModel;
  private ActivityResultLauncher<Intent> launcher;
  private boolean silent;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS); //it also returns size of window.
    getWindow().setExitTransition(new Slide(Gravity.START));//Add effects to transitions.
    viewModel = new ViewModelProvider(this)
        .get(LoginViewModel.class);
    getLifecycle().addObserver(viewModel);
    launcher = registerForActivityResult(new StartActivityForResult(), viewModel::completeSignIn);
    silent = true;
    viewModel
        .getAccount()
        .observe(this, this::handleAccount);
    viewModel.getThrowable()
        .observe(this, this::informFailure);
  }

  private void handleAccount(GoogleSignInAccount account){
    if(account != null){
      Intent intent = new Intent(this, MainActivity.class)
          .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
      //noinspection unchecked
      startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(this).toBundle()); //add the effect created above.
    } else if (silent) {
      silent = false;
      binding = ActivityLoginBinding.inflate(getLayoutInflater());
      binding.signIn.setOnClickListener((v) -> viewModel.startSignIn(launcher)); //Button created in activity_login
      setContentView(binding.getRoot());
    }
  }

  private void informFailure(Throwable throwable){
    if(throwable != null){
      Snackbar.make(binding.getRoot(), R.string.login_failure_message, Snackbar.LENGTH_LONG)
          .show();
    }
  }

}