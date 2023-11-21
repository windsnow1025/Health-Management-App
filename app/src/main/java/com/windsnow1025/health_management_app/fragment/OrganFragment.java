package com.windsnow1025.health_management_app.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import com.windsnow1025.health_management_app.PagerAdapter;
import com.windsnow1025.health_management_app.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OrganFragment extends Fragment {

    View view;
    String organ;

    public OrganFragment(String organ) {
        this.organ = organ;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.organ, container, false);

        Button buttonBack = view.findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, new MainFragment());
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        // Organ Image View
        ImageView imageOrgan = view.findViewById(R.id.organ_imageView);
        // Set source
        switch (organ) {
            case "brain":
                imageOrgan.setImageResource(R.drawable.brain);
                break;
            case "respiratory":
                imageOrgan.setImageResource(R.drawable.respiratory);
                break;
            case "urinary":
                imageOrgan.setImageResource(R.drawable.urinary);
                break;
            case "liver":
                imageOrgan.setImageResource(R.drawable.liver);
                break;
            case "digestive":
                imageOrgan.setImageResource(R.drawable.digestive);
                break;
            case "cardiovascular":
                imageOrgan.setImageResource(R.drawable.cardiovascular);
                break;
            case "musculoskeletal":
                imageOrgan.setImageResource(R.drawable.musculoskeletal);
                break;
            default:
                break;
        }

        // ViewPager2
        ViewPager2 viewPager = view.findViewById(R.id.view_pager);
        List<Fragment> fragments = new ArrayList<>(Arrays.asList(
                new ReportFragment(organ),
                new RecordFragment(organ)
        ));

        ImageButton nextButton = view.findViewById(R.id.next_button);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int nextItem = viewPager.getCurrentItem() + 1;
                viewPager.setCurrentItem(nextItem, true);
            }
        });

        ImageButton prevButton = view.findViewById(R.id.prev_button);
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int prevItem = viewPager.getCurrentItem() - 1;
                viewPager.setCurrentItem(prevItem, true);
            }
        });

        PagerAdapter adapter = new PagerAdapter(getActivity(), fragments);
        viewPager.setAdapter(adapter);
        return view;
    }
}
