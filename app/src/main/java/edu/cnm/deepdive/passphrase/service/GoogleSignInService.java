package edu.cnm.deepdive.passphrase.service;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions.Builder;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import dagger.hilt.android.qualifiers.ApplicationContext;
import edu.cnm.deepdive.passphrase.R;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleEmitter;
import io.reactivex.rxjava3.schedulers.Schedulers;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class GoogleSignInService {

  private static final String BEARER_TOKEN_FORMAT = "Bearer %s";
  private final GoogleSignInClient client;

  @Inject
  GoogleSignInService(@ApplicationContext Context context){
    Resources res = context.getResources();
    @SuppressLint("DiscouragedApi")
    int identifier = res.getIdentifier("service_client_id","string", context.getPackageName());
    String clientId = (identifier != 0) ? res.getString(R.string.service_client_id) : null; //service_client is in local.properties in Gradle scripts
    GoogleSignInOptions.Builder builder = new Builder()
        .requestEmail()
        .requestId()
        .requestProfile(); //get photo
    if(clientId != null && !clientId.isEmpty()){
      builder.requestIdToken(clientId);
    }
    client = GoogleSignIn.getClient(context, builder.build());
  }

  //Refresh account
  public Single<GoogleSignInAccount> refresh(){
    return Single.create((SingleEmitter<GoogleSignInAccount> emitter) ->
        client.silentSignIn()
            .addOnSuccessListener(emitter::onSuccess)
            .addOnFailureListener(emitter::onError)

        )
        .observeOn(Schedulers.io());
  }
  public Single<String> refreshBearerToken(){
    return refresh()
        .map((account) -> String.format(BEARER_TOKEN_FORMAT,account.getIdToken()));
  }

  public void startSignIn(ActivityResultLauncher<Intent> launcher){
    launcher.launch(client.getSignInIntent());
  }

  public Single<GoogleSignInAccount> completeSignIn(ActivityResult result){
    return Single.create((SingleEmitter<GoogleSignInAccount> emitter) -> {
          try {
            Task<GoogleSignInAccount> task =
                GoogleSignIn.getSignedInAccountFromIntent(result.getData());
            GoogleSignInAccount account = task.getResult(ApiException.class);
            emitter.onSuccess(account);
          } catch (ApiException e) {
            emitter.onError(e);
          }
        })
        .observeOn(Schedulers.io());
  }
  public Completable signOut(){
    return Completable.create((emitter) ->
        client
            .signOut()
            .addOnSuccessListener((ignored) -> emitter.onComplete())
            .addOnFailureListener(emitter::onError)
        )
        .subscribeOn(Schedulers.io());
  }
}
