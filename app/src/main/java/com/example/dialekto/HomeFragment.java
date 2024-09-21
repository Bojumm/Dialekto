package com.example.dialekto;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {
    private Spinner dialect1;
    private Spinner dialect2;
    private HashMap<String, String> languageMap;
    private HashMap<String, Locale> localeMap;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("Home");

        View view = inflater.inflate(R.layout.home_fragment, null);
        dialect1 = view.findViewById(R.id.spinnerDialect1);
        dialect2 = view.findViewById(R.id.spinnerDialect2);

        List<String> dialects = new ArrayList<>();
        dialects.add("Tagalog");
        dialects.add("Cebuano");
        dialects.add("Kapampangan");

        // Create an ArrayAdapter using a simple spinner layout
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getContext(), android.R.layout.simple_spinner_item, dialects);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the Spinners
        dialect1.setAdapter(adapter);
        dialect2.setAdapter(adapter);


        return view;


    }


}
