package com.example.jerlib.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.jerlib.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

public class SearchConfigBottomSheet extends BottomSheetDialogFragment {
    ChipGroup searchSortContainer, searchTypeContainer;
    EditText startYearET, endYearET;
    SharedPreferences sharedPref;
    Button applyFilterBtn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.config_bottomdialog, container, false);
        sharedPref = requireContext().getSharedPreferences("FILTERS", Context.MODE_PRIVATE);
        searchSortContainer = view.findViewById(R.id.searchSortContainer);
        searchTypeContainer = view.findViewById(R.id.searchTypeContainer);
        startYearET = view.findViewById(R.id.startYearRange);
        endYearET = view.findViewById(R.id.endYearRange);
        applyFilterBtn = view.findViewById(R.id.applyFilterBtn);

        for (int i=0; i<searchSortContainer.getChildCount();i++){
            Chip chip = (Chip)searchSortContainer.getChildAt(i);
            chip.setChecked(chip.getText().toString().equals(sharedPref.getString("sortBy", "NULL")));
        }

        for (int i=0; i<searchTypeContainer.getChildCount();i++){
            Chip chip = (Chip)searchTypeContainer.getChildAt(i);
            chip.setChecked(chip.getText().toString().equals(sharedPref.getString("searchIn", "NULL")));
        }

        String startYear = sharedPref.getString("yearStart", "2020");
        String endYear = sharedPref.getString("yearEnd", "2024");
        startYearET.setText(startYear);
        endYearET.setText(endYear);

        applyFilterBtn.setOnClickListener(v -> {
            Chip sortChip = ((Chip) view.findViewById(searchSortContainer.getCheckedChipId()));
            Chip searchChip = ((Chip) view.findViewById(searchTypeContainer.getCheckedChipId()));
            String sortBy = sortChip != null ? sortChip.getText().toString() : "A-Z";
            String searchIn = searchChip != null ? searchChip.getText().toString() : "Title";
            String start = startYearET.getText().toString();
            String end = endYearET.getText().toString();

            if (start.isEmpty() || end.isEmpty()) {
                start = "2020";
                end = "2024";
            }

            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("sortBy", sortBy);
            editor.putString("searchIn", searchIn);
            editor.putString("yearStart", start);
            editor.putString("yearEnd", end);
            editor.apply();
            dismiss();
        });


        return view;
    }


}
