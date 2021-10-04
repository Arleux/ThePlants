package com.arleux.byart;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.arleux.byart.calendar.CalendarLastWateringArleuxFragment;
import com.arleux.byart.calendar.CalendarWateringArleuxFragment;

import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

public class PlantFragment extends Fragment {
    final public static String TAG = "plantForPlantFragment";
    private TextView mNameText;
    private TextView mSpeciesText;
    private TextView mPotText;
    private ImageView mPlant_iv;
    private ImageView mPotGreean_iv;
    private ImageView mPotBlue_iv;
    private ImageView mPotRed_iv;
    private Button mWatteringButton;
    private Button mWateringStoryButton;
    private LinearLayout mPotImage;
    private Plant mPlant;
    private EditText plantNameForDialog;
    private String account;
    private PlantsLab mPlantsLab;
    private PlantFragment mPlantFragment;
    private UUID mId;

    private Species mPlantSpecies;
    private RecyclerView mDialogRecyclerView;
    private List<Species> mSpeciesList;
    private AlertDialog picturesDialog;
    private TextView mWateringBottleText;

    public static Fragment newInstance() {
        return new PlantFragment();
    }
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        account = PlantsLab.getIdLogInUser(getContext());
        mPlantsLab = PlantsLab.get(getContext());
        mId = (UUID) getActivity().getIntent().getSerializableExtra(TAG);
        mPlant = mPlantsLab.getPlant(getActivity(), mId);
        mPlantFragment = this;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_plant, container, false);
        mNameText = v.findViewById(R.id.plant_name);
        mSpeciesText = v.findViewById(R.id.plant_species);
        mPotText = v.findViewById(R.id.pot_plant);
        mPlant_iv = v.findViewById(R.id.plant_photo);
        mPotGreean_iv = v.findViewById(R.id.pot_image_green);
        mPotBlue_iv = v.findViewById(R.id.pot_image_blue);
        mPotRed_iv = v.findViewById(R.id.pot_image_red);
        mWatteringButton = v.findViewById(R.id.date_watering_button);
        mWateringStoryButton = v.findViewById(R.id.story_watering_button);
        mPotImage = v.findViewById(R.id.pot_image);
        mWateringBottleText = v.findViewById(R.id.plant_watering_bottle_text);
        mWateringStoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { // кнопка с историей полива
                getActivity().getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, CalendarWateringArleuxFragment.getInstance(mPlant, mPlantFragment)).commit();
            }
        });
        updateView();
        return v;
    }
    @Override
    public void onPause(){
        super.onPause();
        mPlantsLab.updatePlant(mPlant);
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState ){
        super.onViewCreated(view,savedInstanceState );

        mSpeciesList = PlantsLab.getSpeciesList(); //список с видами цветков из строк из ресурсов

        //если тот объект из интента является дефолтным, то я создаю цветок
        if (mPlant.isDefault()){
            View viewForDialogCreate = getLayoutInflater().inflate(R.layout.dialog_create_plant_recycler, null, false);
            mDialogRecyclerView = viewForDialogCreate.findViewById(R.id.dialog_create_recycler_view);
            mDialogRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3)); //ресайклер для изображений в диалоговом окне
            mDialogRecyclerView.setAdapter(new DialogPlantAdapter(mSpeciesList));

            picturesDialog = new AlertDialog.Builder(getContext())
                    .setTitle(R.string.create_plant_title_dialog)
                    .setNeutralButton(R.string.reset_button_back, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            getActivity().onBackPressed();
                        }
                    })
                    .setCancelable(false)
                    .setView(viewForDialogCreate)
                    .create();
            picturesDialog.show();
        }
    }
    private void callDialogForName(){ //диалоговое окно с выбором имени цветка
        View view = getLayoutInflater().inflate(R.layout.dialog_create_plant_name, null, false);
        plantNameForDialog = view.findViewById(R.id.write_plant_name);

        final AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle(R.string.create_plant_title_dialog)
                .setPositiveButton(android.R.string.ok, null)
                .setView(view)
                .setCancelable(false)
                .show();
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (plantNameForDialog.getText().length()>0){
                    //появляется календарик
                    CalendarLastWateringArleuxFragment fragment = CalendarLastWateringArleuxFragment.getInstance(mPlant, mPlantFragment);
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    ft.addToBackStack("calendar");
                    ft.add(R.id.fragment_container, fragment).commit();
                    dialog.dismiss();
                }
                else
                    Toast.makeText(getContext(),R.string.dialog_message_plant_name, Toast.LENGTH_LONG).show();
            }
        });
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int keyCode, KeyEvent keyEvent) {
                if (keyCode == KeyEvent.KEYCODE_BACK){
                    dialog.dismiss();
                    picturesDialog.show(); //диалог с выбором цветков
                }
                return false;
            }
        });
        dialog.show();
    }
    public void updateView() {
        mNameText.setText(mPlant.getName());
        if (!mPlant.isDefault())
            mSpeciesText.setText(mPlant.getSpecies().species());
        mWateringBottleText.setText(String.valueOf(mPlant.getDayForWatering()));
        mPlant_iv.setImageDrawable(mPlant.getSpecies().getImage());
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void addPlant(Species species, LocalDate wateringDay){//добавит цветок в бд
        mPlant.setName(Character.valueOf(plantNameForDialog.getText().toString().charAt(0)).toString().toUpperCase()+plantNameForDialog.getText().toString().substring(1));
        mPlant.setSpecies(species);
        mPlant.setImage(species.getImage()); //получаю id такой фотки из ресурсов
        mPlant.setAccountId(account);
        mPlant.setDefaultWateringInterval(species.getDefaultWateringInterval());
        mPlant.setDayForWatering(wateringDay.plus(species.getDefaultWateringInterval(), ChronoUnit.DAYS));
        mPlant.setIsDefault(false);
        mPlantsLab.addPlant(MainFragment.defaultPlant(getContext(), PlantsLab.getIdLogInUser(getContext()))); // добавляю в бд
    }

    public void updatePlant(Plant plant){
        mPlantsLab.updatePlant(mPlant); //обновляю БД
    }

    public Species getPlantSpecies() {
        return mPlantSpecies;
    } //получение вида цветка после его создания

    private class DialogPlantHolder extends RecyclerView.ViewHolder implements View.OnClickListener { //Холдер для диалог окна с выбором цветков
        private ImageView mChoosePlantImageView;
        private TextView mSpecies;
        private Species localPlantSpecies;
        private Drawable localPlantImage;


        public DialogPlantHolder(LayoutInflater inflater, ViewGroup parent, int viewType) {
        super(inflater.inflate(viewType, parent, false));
        itemView.setOnClickListener(this);

        mChoosePlantImageView = itemView.findViewById(R.id.dialog_plant_image);
        mSpecies = itemView.findViewById(R.id.dialog_plant_species);
    }
    public void bind(Species species) throws IOException {// namePicture - по типу zamik.png

        localPlantSpecies = species; //вид выбранного цветка
        localPlantImage = species.getImage();
        mSpecies.setText(species.species());
        mChoosePlantImageView.setImageDrawable(localPlantImage); //картинка выбранного цветка
    }

        @Override
        public void onClick(View view) {
            picturesDialog.hide(); //диалог с выбором цветков
            mPlantSpecies = localPlantSpecies;
            callDialogForName();
        }
    }

private class DialogPlantAdapter extends RecyclerView.Adapter<DialogPlantHolder>{//Адаптер для диалог окна с выбором цветков
    List<Species> mSpecies;

    public DialogPlantAdapter(List<Species> species){
        mSpecies = species;
    }
    @Override
    public DialogPlantHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = getLayoutInflater();
        return new DialogPlantHolder(inflater, parent, R.layout.dialog_create_plant_image);
    }

    @Override
    public void onBindViewHolder(DialogPlantHolder holder, int position) {
        Species species = mSpecies.get(position);
        try {
            holder.bind(species);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mSpecies.size();
    }
}
}
