package com.example.dialekto;

import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
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
        holder.phraseTextView.setText(item.getInputText());
        holder.translatePhrase.setText(item.getTranslationText());
        holder.inputlanguage.setText(item.getInputLanguage());
        holder.translateLanguage.setText(item.getTranslationLanguage());

        // Set a click listener on the entire card item to show options to remove, delete or cancel
        holder.itemView.setOnClickListener(v -> {
            // Create a PopupMenu when the card item is clicked
            PopupMenu popupMenu = new PopupMenu(v.getContext(), holder.itemView);
            MenuInflater inflater = popupMenu.getMenuInflater();
            inflater.inflate(R.menu.favorite_menu, popupMenu.getMenu()); // Inflate the menu

            // Apply custom background with rounded corners
            try {
                Field fieldMenuHelper = popupMenu.getClass().getDeclaredField("mPopup");
                fieldMenuHelper.setAccessible(true);
                Object menuHelper = fieldMenuHelper.get(popupMenu);

                Method setMenuHelper = menuHelper.getClass().getDeclaredMethod("setMenuBackground", int.class);
                setMenuHelper.setAccessible(true);
                setMenuHelper.invoke(menuHelper, R.drawable.popup_menu_background); // Apply the background
            } catch (Exception e) {
                e.printStackTrace();
            }
            // Set the item click listener to handle actions
            popupMenu.setOnMenuItemClickListener(itemMenu -> {
                switch (itemMenu.getItemId()) {
                    case R.id.remove_favorite:
                        // Remove the item from favorites
                        removeFavorite(position);
                        Toast.makeText(v.getContext(), "Favorite removed.", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.cancel:
                        // Cancel action (do nothing)
                        return true;
                    default:
                        return false;
                }
            });

            // Show the popup menu
            popupMenu.show();
        });
    }

    @Override
    public int getItemCount() {
        return cardItems.size();
    }
    private void removeFavorite(int position) {
        // Remove the item from the list and notify the adapter
        cardItems.remove(position);
        notifyItemRemoved(position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView inputlanguage, phraseTextView, translatePhrase, translateLanguage;
        ImageView heartIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            inputlanguage = itemView.findViewById(R.id.text_language);
            translateLanguage = itemView.findViewById(R.id.translate_language);
            phraseTextView = itemView.findViewById(R.id.text_phrase);
            translatePhrase = itemView.findViewById(R.id.translate_phrase);
            heartIcon = itemView.findViewById(R.id.icon_heart);
        }
    }
}
