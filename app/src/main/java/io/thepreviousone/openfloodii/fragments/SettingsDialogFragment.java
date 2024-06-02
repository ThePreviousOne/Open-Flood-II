package io.thepreviousone.openfloodii.fragments;

import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import org.greenrobot.eventbus.EventBus;

import java.util.Objects;

import io.thepreviousone.openfloodii.R;
import io.thepreviousone.openfloodii.activities.GameActivity;
import io.thepreviousone.openfloodii.activities.MainActivity;
import io.thepreviousone.openfloodii.utils.HeightEvaluator;
import io.thepreviousone.openfloodii.logic.HighScoreManager;
import io.thepreviousone.openfloodii.utils.PixelConverter;
import io.thepreviousone.openfloodii.views.Butter;

/**
 * Activity allowing the user to configure settings.
 */
public class SettingsDialogFragment extends DialogFragment {
    CheckBox colorBlindCheckBox, oldColorsCheckBox;
    int[] boardSizeChoices, numColorsChoices;

    private int selectedBoardSize, selectedNumColors;
    private boolean settingsChanged;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final int closedHeight = PixelConverter.dip2px(290);
        final View layout = requireActivity().getLayoutInflater().inflate(R.layout.dialog_settings, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(layout);
        final AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());

        // Set up the board size RadioGroup
        RadioGroup boardSizeRadioGroup = layout.findViewById(R.id.boardSizeRadioGroup);
        boardSizeChoices = getResources().getIntArray(R.array.boardSizeChoices);
        selectedBoardSize = sp.getInt("board_size",
                getResources().getInteger(R.integer.default_board_size));
        for (final int bs : boardSizeChoices) {
            RadioButton currRadioButton = new RadioButton(getContext());
            currRadioButton.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    getResources().getDimension(R.dimen.small_text_size));
            currRadioButton.setText(String.format("%dx%d", bs, bs));
            boardSizeRadioGroup.addView(currRadioButton);
            if (bs == selectedBoardSize) {
                boardSizeRadioGroup.check(currRadioButton.getId());
            }
            currRadioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SettingsDialogFragment.this.setSelectedBoardSize(bs);
                }
            });
        }

        // Set up the num colors RadioGroup
        RadioGroup numColorsRadioGroup = layout.findViewById(R.id.numColorsRadioGroup);
        numColorsChoices = getResources().getIntArray(R.array.numColorsChoices);
        selectedNumColors = sp.getInt("num_colors",
                getResources().getInteger(R.integer.default_num_colors));
        for (final int nc : numColorsChoices) {
            RadioButton currRadioButton = new RadioButton(getContext());
            currRadioButton.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    getResources().getDimension(R.dimen.small_text_size));
            currRadioButton.setText(String.valueOf(nc));
            numColorsRadioGroup.addView(currRadioButton);
            if (nc == selectedNumColors) {
                numColorsRadioGroup.check(currRadioButton.getId());
            }
            currRadioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SettingsDialogFragment.this.setSelectedNumColors(nc);
                }
            });
        }

        // Set up the apply button
        final Button applyButton = layout.findViewById(R.id.applyButton);
        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSettings();
                dismiss();
            }
        });

        // Set up the color blind checkbox
        colorBlindCheckBox = layout.findViewById(R.id.colorBlindCheckBox);
        colorBlindCheckBox.setChecked(sp.getBoolean("color_blind_mode", false));

        // Set up the old color scheme checkbox
        oldColorsCheckBox = layout.findViewById(R.id.oldColorsCheckBox);
        oldColorsCheckBox.setChecked(sp.getBoolean("use_old_colors", false));

        final Button confirmButton = layout.findViewById(R.id.confirmHighScoresClearButton);
        final Button cancelButton = layout.findViewById(R.id.cancelHighScoresClearButton);
        final RelativeLayout drawer = layout.findViewById(R.id.drawer);

        drawer.setVisibility(View.GONE);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HighScoreManager highScoreManager = new HighScoreManager(
                        PreferenceManager.getDefaultSharedPreferences(getContext()));
                for (int boardSize : getResources().getIntArray(R.array.boardSizeChoices)) {
                    for (int numColors : getResources().getIntArray(R.array.numColorsChoices)) {
                        highScoreManager.removeHighScore(boardSize, numColors);
                    }
                }
                dismiss();

                new Butter(getContext(), R.string.settings_clear_high_scores_toast, Toast.LENGTH_LONG)
                        .setFont(R.font.lenka).setBackgroundColor(0xFFFFFAF6).addJam().show();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                createAnimator(layout, closedHeight).start();

            }
        });

        Button clearHighScoresButton = layout.findViewById(R.id.clearHighScoresButton);
        clearHighScoresButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (drawer.getVisibility() == View.GONE) drawer.setVisibility(View.VISIBLE);
                createAnimator(layout, PixelConverter.dip2px(400)).start();
            }
        });

        return dialog;
    }

    private ValueAnimator createAnimator(final View view, int endHeight) {

        int startHeight = view.getHeight();

        ValueAnimator animation = ValueAnimator.ofObject(
                new HeightEvaluator(view),
                startHeight, endHeight)
                .setDuration(500);

        animation.setInterpolator(new DecelerateInterpolator());
        animation.start();

        return animation;
    }

    private void setSelectedBoardSize(int boardSize) {
        this.selectedBoardSize = boardSize;
    }

    private void setSelectedNumColors(int numColors) {
        this.selectedNumColors = numColors;
    }

    private void saveSettings() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor spEditor = sp.edit();

        // Update boardSize
        int defaultBoardSize = getResources().getInteger(R.integer.default_board_size);
        if (selectedBoardSize != sp.getInt("board_size", defaultBoardSize)) {
            settingsChanged = true;
            spEditor.putInt("board_size", selectedBoardSize);
        }

        // Update number of colors
        int defaultNumColors = getResources().getInteger(R.integer.default_num_colors);
        if (selectedNumColors != sp.getInt("num_colors", defaultNumColors)) {
            settingsChanged = true;
            spEditor.putInt("num_colors", selectedNumColors);
        }

        // Update color blind mode
        boolean selectedColorBlindMode = colorBlindCheckBox.isChecked();
        if (selectedColorBlindMode != sp.getBoolean("color_blind_mode", false)) {
            settingsChanged = true;
            spEditor.putBoolean("color_blind_mode", selectedColorBlindMode);
        }

        // Update whether or not to use the old color scheme
        boolean selectedOldColorScheme = oldColorsCheckBox.isChecked();
        if (selectedOldColorScheme != sp.getBoolean("use_old_colors", false)) {
            settingsChanged = true;
            spEditor.putBoolean("use_old_colors", selectedOldColorScheme);
        }

        spEditor.apply();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (getActivity() instanceof MainActivity) {
            MainActivity mActivity = ((MainActivity) getActivity());
            mActivity.animationResume();
            if (settingsChanged) {
                mActivity.redraw();
            }
        } else if (getActivity() instanceof GameActivity) {
            EventBus.getDefault().post("");
        }
    }
}
