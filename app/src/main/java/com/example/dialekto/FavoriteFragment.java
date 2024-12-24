package com.example.dialekto;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class FavoriteFragment extends Fragment {
    private RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("Favorite");
        View view = inflater.inflate(R.layout.favorite_fragment, null);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Sample data
        List<CardItem> cardItems = new ArrayList<>();
        cardItems.add(new CardItem("Tagalog", "kamusta"));
        cardItems.add(new CardItem("Cebuano", "Naunsa ka"));
        cardItems.add(new CardItem("Ilocano", "Naimbag nga rabii"));

        // Set the adapter
        CardAdapter adapter = new CardAdapter(cardItems);
        recyclerView.setAdapter(adapter);
        return view;
    }
}
