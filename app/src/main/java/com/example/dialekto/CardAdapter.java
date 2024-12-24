package com.example.dialekto;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> {
    private List<CardItem> cardItems;

    public CardAdapter(List<CardItem> cardItems) {
        this.cardItems = cardItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CardItem item = cardItems.get(position);
        holder.languageTextView.setText(item.getLanguage());
        holder.phraseTextView.setText(item.getPhrase());
        holder.heartIcon.setOnClickListener(v -> {
            // Handle heart click
            Toast.makeText(v.getContext(), "Favorited: " + item.getPhrase(), Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return cardItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView languageTextView, phraseTextView;
        ImageView heartIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            languageTextView = itemView.findViewById(R.id.text_language);
            phraseTextView = itemView.findViewById(R.id.text_phrase);
            heartIcon = itemView.findViewById(R.id.icon_heart);
        }
    }
}
