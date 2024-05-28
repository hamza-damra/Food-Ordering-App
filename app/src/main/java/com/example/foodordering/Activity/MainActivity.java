package com.example.foodordering.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import com.example.foodordering.Domain.Category;
import com.example.foodordering.Domain.CategoryAdapter;
import com.example.foodordering.Domain.SliderAdapter;
import com.example.foodordering.Domain.SliderItems;
import com.example.foodordering.R;
import com.example.foodordering.databinding.ActivityMainBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class MainActivity extends BaseActivity {
    ActivityMainBinding binding;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initCategory();
        setVariables();
        initBanner();
    }

    private void initBanner() {
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference("Banners");
        binding.progressBarBanner.setVisibility(View.VISIBLE);
        ArrayList<SliderItems> sliderItems = new ArrayList<>();
        mRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()) {
                            for(DataSnapshot dataSnapshot: snapshot.getChildren()) {
                                SliderItems item = dataSnapshot.getValue(SliderItems.class);
                                if (item != null) {
                                    sliderItems.add(item);
                                } else {
                                    Log.e(TAG, "SliderItems is null for snapshot: " + dataSnapshot);
                                }
                            }
                            if(!sliderItems.isEmpty()) {
                                banners(sliderItems);
                            } else {
                                Log.e(TAG, "SliderItems list is empty after fetching data");
                            }
                        } else {
                            Log.e(TAG, "No data exists for Banners");
                        }
                        binding.progressBarBanner.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "Error fetching Banners: ", error.toException());
                        binding.progressBarBanner.setVisibility(View.GONE);
                    }
                });
    }

    private void banners(ArrayList<SliderItems> sliderItems) {
        SliderAdapter sliderAdapter = new SliderAdapter(sliderItems, binding.viewPager2);
        binding.viewPager2.setAdapter(sliderAdapter);
        binding.viewPager2.setClipToPadding(false);
        binding.viewPager2.setClipChildren(false);
        binding.viewPager2.setOffscreenPageLimit(3);
        binding.viewPager2.getChildAt(0).setOverScrollMode(View.OVER_SCROLL_NEVER);
        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(40));
        binding.viewPager2.setPageTransformer(compositePageTransformer);
    }

    private void setVariables() {
        binding.bottomMenu.setItemSelected(R.id.home, true);
    }

    private void initCategory() {
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference("Category");
        binding.progressBarCategory.setVisibility(View.VISIBLE);
        ArrayList<Category> categoryList = new ArrayList<>();
        mRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()) {
                            for(DataSnapshot dataSnapshot: snapshot.getChildren()) {
                                Category category = dataSnapshot.getValue(Category.class);
                                if (category != null) {
                                    categoryList.add(category);
                                } else {
                                    Log.e(TAG, "Category is null for snapshot: " + dataSnapshot);
                                }
                            }
                            if(!categoryList.isEmpty()) {
                                binding.categoryView.setLayoutManager(new GridLayoutManager(MainActivity.this, 3));
                                binding.categoryView.setAdapter(new CategoryAdapter(categoryList));
                            } else {
                                Log.e(TAG, "Category list is empty after fetching data");
                            }
                        } else {
                            Log.e(TAG, "No data exists for Category");
                        }
                        binding.progressBarCategory.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "Error fetching Category: ", error.toException());
                    }
                });
    }
}
