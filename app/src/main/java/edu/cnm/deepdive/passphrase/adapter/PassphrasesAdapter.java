package edu.cnm.deepdive.passphrase.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import edu.cnm.deepdive.passphrase.R;
import edu.cnm.deepdive.passphrase.adapter.PassphrasesAdapter.Holder;
import edu.cnm.deepdive.passphrase.model.Passphrase;
import java.util.List;


public class PassphrasesAdapter extends RecyclerView.Adapter<Holder> {

  private final List<Passphrase> passphrases;
  private final OnClickListener clickListener;
  private final OnClickListener longClickListener;
  private final LayoutInflater inflater;
  @ColorInt
  private final int evenRowColor;
  @ColorInt
  private final int oddRowColor;

  public PassphrasesAdapter(@NonNull Context context,@NonNull List<Passphrase> passphrases,
      @NonNull OnClickListener clickListener,@NonNull OnClickListener longClickListener) {
    this.passphrases = passphrases;
    this.clickListener = clickListener;
    this.longClickListener = longClickListener;
    inflater = LayoutInflater.from(context);
    evenRowColor = context.getColor(R.color.even_row_color);
    oddRowColor = context.getColor(R.color.odd_row_color);
  }

  @NonNull
  @Override
  public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    return new Holder(inflater.inflate(R.layout.item_passphrases, parent, false));
  }

  @Override
  public void onBindViewHolder(@NonNull Holder holder, int position) {
    holder.bind(position);
  }

  @Override
  public int getItemCount() {
    return passphrases.size();
  }

  public class Holder extends RecyclerView.ViewHolder {

    private Holder(
        @NonNull View itemView) {
      super(itemView);
    }

    private void bind(int position) {
      Passphrase passphrase = passphrases.get(position);
      ((TextView) itemView).setText(passphrase.getName());
      itemView.setBackgroundColor((position % 2 == 0) ? evenRowColor : oddRowColor);
      itemView.setOnClickListener((v) -> clickListener.onClick(v, position, passphrase));
      itemView.setOnLongClickListener((v) -> {
        longClickListener.onClick(v, position,
            passphrase);
        return true;
      });
    }
  }

  @FunctionalInterface
  public interface OnClickListener {
    void onClick(View view, int position, Passphrase passphrase);
  }

}
