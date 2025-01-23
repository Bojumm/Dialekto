package com.example.dialekto;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class FavoriteFragment extends Fragment {
    private RecyclerView recyclerView;

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("Favorite");
        View view = inflater.inflate(R.layout.favorite_fragment, null);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Pass the context to getInstance()
        FavoritesManager favoritesManager = FavoritesManager.getInstance(requireContext());

        // Get the shared favorites list
        List<CardItem> favoriteItems = favoritesManager.getFavoriteItems();

        // Set the adapter
        CardAdapter adapter = new CardAdapter(favoriteItems);
        recyclerView.setAdapter(adapter);

        return view;
    }
}
