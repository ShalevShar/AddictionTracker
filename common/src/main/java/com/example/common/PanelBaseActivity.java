package com.example.common;

import static com.example.common.SharedPreferencesManager.DATE_JOINED;
import static com.example.common.SharedPreferencesManager.KEY_FIRST_LAUNCH_DATE;
import static com.example.common.SharedPreferencesManager.KEY_REHAB_TARGET;
import static com.example.common.SharedPreferencesManager.USER_AGE;
import static com.example.common.SharedPreferencesManager.USER_NAME;

import android.animation.ValueAnimator;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Window;

import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textview.MaterialTextView;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class PanelBaseActivity extends AppCompatActivity {
    private AppCompatImageButton main_BTN_tllButton;
    private LinearProgressIndicator main_PRGRS;
    private MaterialTextView main_LBL_percentageProgress;
    private AppCompatImageButton main_BTN_trrButton;
    private MaterialTextView main_MTV_periodInfo;
    private AppCompatImageView main_IMG_center_light;
    private AppCompatTextView main_LBL_dayCount;
    private AppCompatImageButton container_BTN_left;
    private AppCompatImageButton container_BTN_center;
    private AppCompatImageButton container_BTN_right;
    private AppCompatImageButton main_BTN_blButton;
    private AppCompatImageButton main_BTN_bcButton;
    private AppCompatImageButton main_BTN_brButton;
    private View main_carousel_bar_left;
    private View main_carousel_bar_center;
    private View main_carousel_bar_right;
    private Boolean isMinimized_BTN_trr = true;
    private List<RelapseItem> relapseItemList;
    private RelapseAdapter relapseAdapter;
    private SharedPreferencesManager sharedPreferencesManager;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panel_base);
        sharedPreferencesManager = new SharedPreferencesManager(this);
        findView();
        updateUI();
        main_BTN_tllButton.setOnClickListener(v -> showTips());
        main_BTN_trrButton.setOnClickListener(v -> showTargetPeriod());
        container_BTN_left.setOnClickListener(v -> showSetTargetDialog(null));
        container_BTN_center.setOnClickListener(v -> updateContainerUI(container_BTN_center));
        container_BTN_right.setOnClickListener(v -> showRelapseDialog());
        main_BTN_blButton.setOnClickListener(v -> updateBottomBarUI(main_BTN_blButton));
        main_BTN_bcButton.setOnClickListener(v -> showSettingsDialog());
        main_BTN_brButton.setOnClickListener(v -> showPersonalDialog());
    }

    private void showPersonalDialog() {
        updateBottomBarUI(main_BTN_brButton);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_personal, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        dialog.show();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            // Get screen height
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int screenHeight = displayMetrics.heightPixels;

            // Set dialog height to be a bit less than the screen height to create bottom margin
            WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
            layoutParams.dimAmount = 0.0f;
            layoutParams.gravity = Gravity.TOP;
            layoutParams.height = screenHeight - 400; // Adjust this value to set the bottom margin
            dialog.getWindow().setAttributes(layoutParams);
        }

        AppCompatTextView dialog_personal_name = dialogView.findViewById(R.id.dialog_personal_name);
        AppCompatTextView dialog_personal_age = dialogView.findViewById(R.id.dialog_personal_age);
        AppCompatTextView dialog_personal_targetDate = dialogView.findViewById(R.id.dialog_personal_targetDate);
        AppCompatImageButton dialog_personal_back = dialogView.findViewById(R.id.dialog_personal_back);
        dialog_personal_name.setText(sharedPreferencesManager.sharedPreferences.getString(USER_NAME, "Name"));
        dialog_personal_age.setText(sharedPreferencesManager.sharedPreferences.getString(USER_AGE, "00"));
        dialog_personal_targetDate.setText(sharedPreferencesManager.sharedPreferences.getString(DATE_JOINED, "00/00/00"));
        dialog_personal_back.setOnClickListener(v -> dialog.dismiss());
        dialog.setOnDismissListener(d -> {
            main_BTN_brButton.setBackgroundTintList(getResources().getColorStateList(R.color.grey_bar, null));
            main_BTN_blButton.setBackgroundTintList(getResources().getColorStateList(R.color.blue_bar, null));
        });
        main_BTN_brButton.setBackgroundTintList(getResources().getColorStateList(R.color.blue_bar, null));
    }

    private void showTargetPeriod() {
        Log.d("showTargetPeriod", "Entering showTargetPeriod() method");
        if (isMinimized_BTN_trr) {
            isMinimized_BTN_trr = false;
            main_BTN_trrButton.setBackgroundTintList(getResources().getColorStateList(R.color.blue_bar, null));
            displayMonthsPassed();
            int targetWidth = getResources().getDimensionPixelSize(R.dimen.expanded_width); // Define your expanded width in resources
            animateWidthToLeft(main_BTN_trrButton, targetWidth);
        } else {
            isMinimized_BTN_trr = true;
            main_BTN_trrButton.setBackgroundTintList(getResources().getColorStateList(R.color.white, null));

            // Define your original width in resources or use a fixed value
            int originalWidth = getResources().getDimensionPixelSize(R.dimen.minimized_width);
            animateWidthToRight(main_BTN_trrButton, originalWidth);
            main_MTV_periodInfo.setVisibility(View.INVISIBLE);
        }
    }

    private void displayMonthsPassed() {
        main_MTV_periodInfo.setVisibility(View.VISIBLE);

        String startDateString = sharedPreferencesManager.sharedPreferences.getString(SharedPreferencesManager.KEY_FIRST_LAUNCH_DATE, null);
        int rehabTarget = sharedPreferencesManager.sharedPreferences.getInt(String.valueOf(SharedPreferencesManager.KEY_REHAB_TARGET), 12); // Default to 12 months

        if (startDateString != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            try {
                Date startDate = sdf.parse(startDateString);
                Date currentDate = Calendar.getInstance().getTime();

                long diffInMillis = currentDate.getTime() - startDate.getTime();
                long monthsPassed = TimeUnit.MILLISECONDS.toDays(diffInMillis) / 30;

                monthsPassed = Math.min(monthsPassed, rehabTarget);

                String periodInfo = monthsPassed + "/" + rehabTarget + " months";
                Log.d("PeriodInfo", periodInfo);
                main_MTV_periodInfo.setText(periodInfo);
                main_MTV_periodInfo.setVisibility(View.VISIBLE); // Show the TextView with calculated information
            } catch (ParseException e) {
                e.printStackTrace();
                Log.e("displayMonthsPassed", "ParseException: " + e.getMessage());
            }
        } else {
            main_MTV_periodInfo.setText("");
            main_MTV_periodInfo.setVisibility(View.INVISIBLE);
        }
    }
    private void animateWidthToLeft(final View view, int targetWidth) {

        int currentWidth = view.getWidth();

        ValueAnimator animator = ValueAnimator.ofInt(currentWidth, targetWidth);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int animatedValue = (int) animation.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                layoutParams.width = animatedValue;
                view.setLayoutParams(layoutParams);
            }
        });

        animator.setDuration(300);
        animator.start();
    }

    private void animateWidthToRight(final View view, int targetWidth) {
        int currentWidth = view.getWidth();

        ValueAnimator animator = ValueAnimator.ofInt(currentWidth, targetWidth);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int animatedValue = (int) animation.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                layoutParams.width = animatedValue;
                view.setLayoutParams(layoutParams);
            }
        });

        animator.setDuration(300); // Adjust duration as needed
        animator.start();
    }
    private void updateUI() {
        updateStatusBarUI();
        if (sharedPreferencesManager.isFirstTimeLaunch()) {
            showWelcomeMessage();
        }
        loadUserData();
    }

    private void updateStatusBarUI() {
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.grey_bg));
    }

    private void showWelcomeMessage() {
        LayoutInflater inflater = getLayoutInflater();
        View welcomeDialogView = inflater.inflate(R.layout.dialog_welcome, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(welcomeDialogView);
        builder.setCancelable(false);

        AlertDialog welcomeDialog = builder.create();
        welcomeDialog.show();

        AppCompatTextView dialog_welcome_header = welcomeDialogView.findViewById(R.id.dialog_welcome_header);
        AppCompatEditText nameEditText = welcomeDialogView.findViewById(R.id.dialog_welcome_EDT_name);
        AppCompatEditText ageEditText = welcomeDialogView.findViewById(R.id.dialog_welcome_EDT_age);
        AppCompatImageButton continueButton = welcomeDialogView.findViewById(R.id.dialog_welcome_BTN_continue);

        continueButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString();
            String ageString = ageEditText.getText().toString();

            if (name.isEmpty() || ageString.isEmpty()) {
                Toast.makeText(PanelBaseActivity.this, "Please enter your name and age.", Toast.LENGTH_SHORT).show();
            } else {
                int age = Integer.parseInt(ageString);
                sharedPreferencesManager.saveName(name);
                sharedPreferencesManager.saveAge(ageString);
                sharedPreferencesManager.saveDateJoined();
                welcomeDialog.dismiss();
                showSetTargetDialog(null);
            }
        });
    }

    private void showSetTargetDialog(Dialog welcomeDialog) {
        updateContainerUI(container_BTN_left);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Set Rehab Target")
                .setMessage("Select a target in months")
                .setCancelable(false);
        Spinner spinner = new Spinner(this);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.rehab_targets, R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        builder.setView(spinner);

        builder.setPositiveButton("OK", null);

        builder.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.dismiss();
        });

        AlertDialog targetDialog = builder.create();
        targetDialog.setOnShowListener(dialog -> {
            Button positiveButton = targetDialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(view -> {
                int selectedTarget = Integer.parseInt(spinner.getSelectedItem().toString());
                sharedPreferencesManager.saveRehabTarget(selectedTarget);
                saveFirstLaunchDate();
                Toast.makeText(this, "Rehab target was set successfully", Toast.LENGTH_SHORT).show();
                if (welcomeDialog != null) {
                    welcomeDialog.dismiss();
                }
                targetDialog.dismiss();
            });

            targetDialog.setOnDismissListener(d -> {
                main_carousel_bar_left.setBackgroundTintList(getResources().getColorStateList(R.color.grey_bar, null));
                main_carousel_bar_center.setBackgroundTintList(getResources().getColorStateList(R.color.blue_bar, null));
                main_LBL_dayCount.setTextColor(ContextCompat.getColor(this, R.color.blue_bar));
                container_BTN_left.setImageTintList(getResources().getColorStateList(R.color.white, null));
                container_BTN_center.setImageTintList(getResources().getColorStateList(R.color.blue_bar, null));
            });
            positiveButton.setBackgroundColor(getResources().getColor(R.color.blue_bar));
            positiveButton.setTextColor(getResources().getColor(android.R.color.white));
        });

        targetDialog.show();

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(targetDialog.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        targetDialog.getWindow().setAttributes(lp);
    }
    private void saveFirstLaunchDate() {
        Calendar calendar = Calendar.getInstance();
        String currentDate = DateFormat.getDateInstance().format(calendar.getTime());

        sharedPreferencesManager.saveFirstLaunchDate(currentDate);
        loadUserData();
    }

    private void showSettingsDialog() {
        updateBottomBarUI(main_BTN_bcButton);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_settings, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        dialog.show();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
            layoutParams.dimAmount = 0.0f;
            dialog.getWindow().setAttributes(layoutParams);
        }

        AppCompatButton enlargeFontOption = dialogView.findViewById(R.id.dialog_option_enlarge_font);
        AppCompatButton resetValuesOption = dialogView.findViewById(R.id.dialog_option_reset_values);
        AppCompatImageButton dialog_option_back = dialogView.findViewById(R.id.dialog_option_back);


        enlargeFontOption.setOnClickListener(v -> {
            Toast.makeText(this, "Enlarge Font selected", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        resetValuesOption.setOnClickListener(v -> {
            Toast.makeText(this, "Reset All Values selected", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        dialog_option_back.setOnClickListener(v -> dialog.dismiss());
        dialog.setOnDismissListener(d -> {
            main_BTN_bcButton.setBackgroundTintList(getResources().getColorStateList(R.color.grey_bar, null));
            main_BTN_blButton.setBackgroundTintList(getResources().getColorStateList(R.color.blue_bar, null));

        });

        main_BTN_bcButton.setBackgroundTintList(getResources().getColorStateList(R.color.blue_bar, null));
    }


    private void showRelapseDialog() {
        updateContainerUI(container_BTN_right);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_relapse, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        dialog.show();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
            layoutParams.dimAmount = 0.0f;
            dialog.getWindow().setAttributes(layoutParams);
        }

        RecyclerView dialog_relapse_RCV = dialogView.findViewById(R.id.dialog_relapse_RCV);
        AppCompatImageButton dialog_relapse_add = dialogView.findViewById(R.id.dialog_relapse_add);
        AppCompatImageButton dialog_relapse_back = dialogView.findViewById(R.id.dialog_relapse_back);

        dialog_relapse_RCV.setLayoutManager(new LinearLayoutManager(this));
        dialog_relapse_RCV.setAdapter(relapseAdapter);

        dialog_relapse_add.setOnClickListener(v -> showAddRelapseItemDialog());

        dialog_relapse_back.setOnClickListener(v -> dialog.dismiss());
        dialog.setOnDismissListener(d -> {
            main_carousel_bar_right.setBackgroundTintList(getResources().getColorStateList(R.color.grey_bar, null));
            main_carousel_bar_center.setBackgroundTintList(getResources().getColorStateList(R.color.blue_bar, null));
            container_BTN_right.setImageTintList(getResources().getColorStateList(R.color.white, null));
            container_BTN_center.setImageTintList(getResources().getColorStateList(R.color.blue_bar, null));
            main_LBL_dayCount.setTextColor(ContextCompat.getColor(this, R.color.blue_bar));
        });

        main_carousel_bar_right.setBackgroundTintList(getResources().getColorStateList(R.color.blue_bar, null));
    }

    private void showAddRelapseItemDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_relapse_item, null);
        builder.setView(dialogView);

        AppCompatEditText editTextDate = dialogView.findViewById(R.id.editTextDate);;
        Calendar calendar = Calendar.getInstance();
        editTextDate.setOnClickListener(v -> showDatePickerDialog(calendar, PanelBaseActivity.this, editTextDate));
        AppCompatEditText editTextDescription = dialogView.findViewById(R.id.editTextDescription);
        AppCompatEditText editTextDose = dialogView.findViewById(R.id.editTextDose);

        builder.setPositiveButton("Add", (dialog, which) -> {

            String date = Objects.requireNonNull(editTextDate.getText()).toString();
            String description = Objects.requireNonNull(editTextDescription.getText()).toString();
            String dose = Objects.requireNonNull(editTextDose.getText()).toString();

            RelapseItem newItem = new RelapseItem(date, description, dose);
            relapseAdapter.addItem(newItem);
            sharedPreferencesManager.saveRelapseItems(relapseItemList);

        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();

    }
    private void showDatePickerDialog(Calendar calendar, Context context, AppCompatEditText editTextDate) {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                context,
                R.style.CustomDatePickerDialog,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    calendar.set(selectedYear, selectedMonth, selectedDay);
                    editTextDate.setText(selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear);
                },
                year, month, day);

        datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
        datePickerDialog.show();
    }

    private void updateBottomBarUI(AppCompatImageButton clickedButton) {

        main_BTN_blButton.setBackgroundTintList(getResources().getColorStateList(R.color.grey_bar, null));
        main_BTN_bcButton.setBackgroundTintList(getResources().getColorStateList(R.color.grey_bar, null));
        main_BTN_brButton.setBackgroundTintList(getResources().getColorStateList(R.color.grey_bar, null));

        if (clickedButton == main_BTN_blButton) {
            main_BTN_blButton.setBackgroundTintList(getResources().getColorStateList(R.color.blue_bar, null));
        } else if (clickedButton == main_BTN_bcButton) {
            main_BTN_bcButton.setBackgroundTintList(getResources().getColorStateList(R.color.blue_bar, null));
        } else if (clickedButton == main_BTN_brButton) {
            main_BTN_brButton.setBackgroundTintList(getResources().getColorStateList(R.color.blue_bar, null));
        }
    }

    private void showTips() {
        main_BTN_tllButton.setBackgroundTintList(getResources().getColorStateList(R.color.blue_bar, null));

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.tips_header))
                .setMessage(getString(R.string.tips))
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(d -> {
            int blueColor = getResources().getColor(R.color.blue_bar, null);
            int textViewId = getResources().getIdentifier("alertTitle", "id", "android");
            if (textViewId > 0) {
                AppCompatTextView textView = dialog.findViewById(textViewId);
                if (textView != null) {
                    textView.setTextColor(blueColor);
                }
            }

            int titleDividerId = getResources().getIdentifier("titleDivider", "id", "android");
            View titleDivider = dialog.findViewById(titleDividerId);
            if (titleDivider != null) {
                titleDivider.setBackgroundColor(blueColor);
            }
        });

        dialog.setOnDismissListener(d -> {
            main_BTN_tllButton.setBackgroundTintList(getResources().getColorStateList(R.color.grey_bar, null));
        });

        dialog.show();
    }

    private void loadUserData() {
        relapseItemList = new ArrayList<>();
        relapseAdapter = new RelapseAdapter(relapseItemList);
        List<RelapseItem> savedItems = sharedPreferencesManager.loadRelapseItems();
        if (savedItems != null) {
            relapseItemList.addAll(savedItems);
            relapseAdapter.notifyDataSetChanged();
        }

        String firstLaunchDateStr = sharedPreferencesManager.sharedPreferences.getString(KEY_FIRST_LAUNCH_DATE, "Sep 15, 2023"); // Default date for testing
        int rehabTarget = sharedPreferencesManager.sharedPreferences.getInt(String.valueOf(KEY_REHAB_TARGET), 12); // Default to 12 months
        Log.d("FirstLaunchDate", firstLaunchDateStr);
        Log.d("RehabTarget", String.valueOf(rehabTarget));

        if (firstLaunchDateStr != null) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
                Date firstLaunchDate = sdf.parse(firstLaunchDateStr);
                Calendar firstLaunchCalendar = Calendar.getInstance();
                firstLaunchCalendar.setTime(firstLaunchDate);

                Calendar currentCalendar = Calendar.getInstance();

                int elapsedMonths = getMonthsDifference(firstLaunchCalendar, currentCalendar);

                int progressPercentage = Math.min((elapsedMonths * 100) / rehabTarget, 100); // Ensure it doesn't exceed 100%

                main_PRGRS.setProgress(progressPercentage);
                main_LBL_percentageProgress.setText(progressPercentage + "%");

                long elapsedTimeInMillis = currentCalendar.getTimeInMillis() - firstLaunchCalendar.getTimeInMillis();
                int elapsedDays = (int) (elapsedTimeInMillis / (1000 * 60 * 60 * 24));

                main_LBL_dayCount.setText(String.valueOf(elapsedDays + 1));

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            main_PRGRS.setProgress(0);
            main_LBL_percentageProgress.setText("0%");
            main_LBL_dayCount.setText("0");
        }
    }

    private int getMonthsDifference(Calendar startDate, Calendar endDate) {
        int years = endDate.get(Calendar.YEAR) - startDate.get(Calendar.YEAR);
        int months = endDate.get(Calendar.MONTH) - startDate.get(Calendar.MONTH);
        return years * 12 + months;
    }

    private void findView() {
        main_BTN_tllButton = findViewById(R.id.main_BTN_tllButton);
        main_PRGRS = findViewById(R.id.main_PRGRS);
        main_LBL_percentageProgress = findViewById(R.id.main_LBL_percentageProgress);
        main_BTN_trrButton = findViewById(R.id.main_BTN_trrButton);
        main_MTV_periodInfo = findViewById(R.id.main_MTV_periodInfo);
        main_LBL_dayCount = findViewById(R.id.main_LBL_dayCount);
        main_IMG_center_light = findViewById(R.id.main_IMG_center_light);
        container_BTN_left = findViewById(R.id.container_BTN_left);
        container_BTN_center = findViewById(R.id.container_BTN_center);
        container_BTN_right = findViewById(R.id.container_BTN_right);
        main_BTN_blButton = findViewById(R.id.main_BTN_blButton);
        main_BTN_bcButton = findViewById(R.id.main_BTN_bcButton);
        main_BTN_brButton = findViewById(R.id.main_BTN_brButton);
        main_carousel_bar_left = findViewById(R.id.main_carousel_bar_left);
        main_carousel_bar_center = findViewById(R.id.main_carousel_bar_center);
        main_carousel_bar_right = findViewById(R.id.main_carousel_bar_right);
    }

    private void updateContainerUI(AppCompatImageButton clickedButton) {
        LinearLayout.LayoutParams leftParams = (LinearLayout.LayoutParams) container_BTN_left.getLayoutParams();
        LinearLayout.LayoutParams centerParams = (LinearLayout.LayoutParams) container_BTN_center.getLayoutParams();
        LinearLayout.LayoutParams rightParams = (LinearLayout.LayoutParams) container_BTN_right.getLayoutParams();

        leftParams.weight = 0.5f;
        centerParams.weight = 2f;
        rightParams.weight = 0.5f;

        container_BTN_left.setLayoutParams(leftParams);
        container_BTN_center.setLayoutParams(centerParams);
        container_BTN_right.setLayoutParams(rightParams);

        main_carousel_bar_left.setBackgroundTintList(getResources().getColorStateList(R.color.grey_bar, null));
        main_carousel_bar_center.setBackgroundTintList(getResources().getColorStateList(R.color.grey_bar, null));
        main_LBL_dayCount.setTextColor(ContextCompat.getColor(this, R.color.white));
        main_carousel_bar_right.setBackgroundTintList(getResources().getColorStateList(R.color.grey_bar, null));
        container_BTN_left.setImageTintList(getResources().getColorStateList(R.color.white, null));
        container_BTN_center.setImageTintList(getResources().getColorStateList(R.color.white, null));
        container_BTN_right.setImageTintList(getResources().getColorStateList(R.color.white, null));

        if (clickedButton == container_BTN_left) {
            main_carousel_bar_left.setBackgroundTintList(getResources().getColorStateList(R.color.blue_bar, null));
            container_BTN_left.setImageTintList(getResources().getColorStateList(R.color.blue_bar, null));
        } else if (clickedButton == container_BTN_center) {
            main_carousel_bar_center.setBackgroundTintList(getResources().getColorStateList(R.color.blue_bar, null));
            container_BTN_center.setImageTintList(getResources().getColorStateList(R.color.blue_bar, null));
            main_LBL_dayCount.setTextColor(ContextCompat.getColor(this, R.color.blue_bar));
        } else if (clickedButton == container_BTN_right) {
            main_carousel_bar_right.setBackgroundTintList(getResources().getColorStateList(R.color.blue_bar, null));
            container_BTN_right.setImageTintList(getResources().getColorStateList(R.color.blue_bar, null));
        }
        container_BTN_left.setLayoutParams(leftParams);
        container_BTN_center.setLayoutParams(centerParams);
        container_BTN_right.setLayoutParams(rightParams);
    }
}