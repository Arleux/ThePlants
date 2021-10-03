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
import java.io.InputStream;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlantFragment extends Fragment {
    final public static String TAG = "plantForPlantFragment";
    private TextView mNameText;
    private TextView mSpeciesText;
    private TextView mPotText;
    private ImageView mPhoto;
    private ImageView mPotGreean;
    private ImageView mPotBlue;
    private ImageView mPotRed;
    private Button mWatteringButton;
    private Button mWateringStoryButton;
    private LinearLayout mPotImage;
    private Plant mPlant;
    private EditText plantNameForDialog;
    private String account;
    private PlantsLab mPlantsLab;
    private PlantFragment mPlantFragment;
    private UUID mId;

    private Picture mPlantPicture;
    private Species mPlantSpecies;
    private RecyclerView mDialogRecyclerView;
    private List<String> mAssetsPicturesList;
    private List<Species> mSpeciesList;
    private AlertDialog picturesDialog;
    private TextView mWateringBottleText;

    public static Fragment newInstance() {
        return new PlantFragment();
    }
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        account = PlantsLab.getIdLogInUser(getActivity());
        mPlantsLab = PlantsLab.get(getActivity());
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
        mPhoto = v.findViewById(R.id.plant_photo);
        mPotGreean = v.findViewById(R.id.pot_image_green);
        mPotBlue = v.findViewById(R.id.pot_image_blue);
        mPotRed = v.findViewById(R.id.pot_image_red);
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

        mAssetsPicturesList = new ArrayList<>();
        mSpeciesList = PlantsLab.getSpeciesList(); //список с видами цветков из строк из ресурсов

        try {
            String[] pictures = getActivity().getAssets().list("plantsPictures"); //загружаю все изображения из assets пока в виде названия изображения
            for (String picture: pictures){
                mAssetsPicturesList.add(picture);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //если тот объект из интента является дефолтным, то я создаю цветок
        if (mPlant.isDefault()){
            View viewForDialogCreate = getLayoutInflater().inflate(R.layout.dialog_create_plant_recycler, null, false);
            mDialogRecyclerView = viewForDialogCreate.findViewById(R.id.dialog_create_recycler_view);
            mDialogRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3)); //ресайклер для изображений в диалоговом окне
            mDialogRecyclerView.setAdapter(new DialogPlantAdapter(mAssetsPicturesList, mSpeciesList));

            picturesDialog = new AlertDialog.Builder(getActivity())
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

        final AlertDialog dialog = new AlertDialog.Builder(getActivity())
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
                    Toast.makeText(getActivity(),R.string.dialog_message_plant_name, Toast.LENGTH_LONG).show();
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
        try {
            mPhoto.setImageDrawable(Drawable.createFromStream(getActivity().getAssets().open(mPlant.getPhoto()), null));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void addPlant(Picture picture, Species species, LocalDate wateringDay){//добавит цветок в бд
        mPlant.setName(Character.valueOf(plantNameForDialog.getText().toString().charAt(0)).toString().toUpperCase()+plantNameForDialog.getText().toString().substring(1));
        mPlant.setSpecies(species);
        mPlant.setPhoto(picture.getPathName()); //получаю id такой фотки из ресурсов
        mPlant.setAccountId(account);
        mPlant.setDefaultWateringInterval(species.getDefaultWateringInterval());
        mPlant.setDayForWatering(wateringDay.plus(species.getDefaultWateringInterval(), ChronoUnit.DAYS));
        mPlant.setIsDefault(false);
        mPlantsLab.addPlant(MainFragment.defaultPlant(PlantsLab.getIdLogInUser(getContext()))); // добавляю в бд
    }

    public void updatePlant(Plant plant){
        mPlantsLab.updatePlant(mPlant); //обновляю БД
    }

    public Picture getPlantPicture() {
        return mPlantPicture;
    } //получение картинки цветка после его создания

    public Species getPlantSpecies() {
        return mPlantSpecies;
    } //получение вида цветка после его создания

    private class DialogPlantHolder extends RecyclerView.ViewHolder implements View.OnClickListener { //Холдер для диалог окна с выбором цветков
        private ImageView mPlantImage;
        private TextView mSpecies;
        private Picture localPlantPicture;
        private Species localPlantSpecies;


        public DialogPlantHolder(LayoutInflater inflater, ViewGroup parent, int viewType) {
        super(inflater.inflate(viewType, parent, false));
        itemView.setOnClickListener(this);

        mPlantImage = itemView.findViewById(R.id.dialog_plant_image);
        mSpecies = itemView.findViewById(R.id.dialog_plant_species);
    }
    public void bind(String namePicture, Species species) throws IOException {// namePicture - по типу zamik.png
        InputStream inputStream = getContext().getAssets().open("plantsPictures/"+namePicture);
        mPlantImage.setImageDrawable(Drawable.createFromStream(inputStream, null));
        mSpecies.setText(species.species());
        localPlantSpecies = species; //вид выбранного цветка
        localPlantPicture = new Picture(namePicture); //картинка выбранного цветка
    }

        @Override
        public void onClick(View view) {
            picturesDialog.hide(); //диалог с выбором цветков
            mPlantPicture = localPlantPicture;
            mPlantSpecies = localPlantSpecies;
            callDialogForName();
        }
    }

private class DialogPlantAdapter extends RecyclerView.Adapter<DialogPlantHolder>{//Адаптер для диалог окна с выбором цветков
    List<String> mPictures;
    List<Species> mSpecies;

    public DialogPlantAdapter(List<String> pictures, List<Species> species){
        mPictures = pictures;
        mSpecies = species;
    }
    @Override
    public DialogPlantHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = getLayoutInflater();
        return new DialogPlantHolder(inflater, parent, R.layout.dialog_create_plant_image);
    }

    @Override
    public void onBindViewHolder(DialogPlantHolder holder, int position) {
        String namePicture = mPictures.get(position);
        Species species = mSpecies.get(position);
        try {
            holder.bind(namePicture, species);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mAssetsPicturesList.size();
    }
}
}
