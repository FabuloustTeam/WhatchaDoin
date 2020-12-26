package com.example.whatchadoin.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.whatchadoin.R;
import com.example.whatchadoin.ui.grocery.GroceryActivity;
import com.example.whatchadoin.ui.statistic.StatisticActivity;
import com.example.whatchadoin.ui.tag.TagActivity;

public class DashboardFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;

    Button tag;
    Button grocery;
    Button statistic;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
//        final TextView textView = root.findViewById(R.id.text_dashboard);
//        dashboardViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });
        getElement(root);

        tag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), TagActivity.class);
                intent.putExtra("", "tag");
                startActivity(intent);
            }
        });

        grocery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), GroceryActivity.class);
                intent.putExtra("", "grocery");
                startActivity(intent);
            }
        });

        statistic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), StatisticActivity.class);
                intent.putExtra("", "statistic");
                startActivity(intent);
            }
        });

        return root;
    }

    private void getElement(View root) {
        tag = (Button) root.findViewById(R.id.btnTag);
        grocery = (Button) root.findViewById(R.id.btnGrocery);
        statistic = (Button) root.findViewById(R.id.btnStatistic);
    }
}