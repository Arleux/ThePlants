package com.arleux.byart;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static com.arleux.byart.ThePlantsFragment.hideKeyboard;

public class MainFragment extends Fragment {
    public static String DEFAULT_PHOTO = "default/ic_account_add.png";
    private int mAdapterPosition;
    private RecyclerView mPlantRecyclerView;
    private MainAdapter mAdapter;
    private List<Plant> plants;
    private Callbacks mCallbacks;
    private TextView mPlantName;

    public static Fragment newInstance() {
        return new MainFragment();
    }

    public interface Callbacks{ //для отправки интента на открытие PlantFragment
        void onPlantSelected(Plant plant);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallbacks = (Callbacks) context;
    }
    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        PlantsLab plantsLab = PlantsLab.get(getActivity());
        plants = plantsLab.getPlants(getActivity(), PlantsLab.getIdLogInUser(getActivity()));
        if (plants == null)
            plantsLab.addPlant(defaultPlant(PlantsLab.getIdLogInUser(getActivity()))); //добавляю дефолтный цветок
        setHasOptionsMenu(true);
    }
    @Override
    public void onResume(){
        super.onResume();
        updateUI();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main_list, container, false);
        hideKeyboard(getActivity(), container);  //для того чтобы спрятать клавиатуру при касании пальцем по экрану
        mPlantRecyclerView =(RecyclerView) v.findViewById(R.id.main_recycler);
        mPlantRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        updateUI();

        return v;
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings_menu:
                startActivity(SettingsMainActivity.newIntent(getActivity()));
        }
        return true;
    }

    public void updateUI(){
        PlantsLab plantsLab = PlantsLab.get(getActivity());
        List<Plant> plants = plantsLab.getPlants(getActivity(), PlantsLab.getIdLogInUser(getActivity()));
        if(mAdapter == null){
            mAdapter = new MainAdapter(plants);
            mPlantRecyclerView.setAdapter(mAdapter);
        }
        else {
            mAdapter.setPlants(plants);
            mAdapter.notifyDataSetChanged();
        }
    }

    private class MainHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Plant mPlant;
        private ImageView mImage;

        public MainHolder(LayoutInflater inflater, ViewGroup parent, int viewType) {
            super(inflater.inflate(viewType, parent, false));
            itemView.setOnClickListener(this);

            mPlantName = (TextView) itemView.findViewById(R.id.plant_name_text_main_item);
            mImage = (ImageView) itemView.findViewById(R.id.default_pic_id);
        }
        public void bind(Plant plant) throws IOException {
            mPlant = plant;
            mImage.setImageDrawable(Drawable.createFromStream(getActivity().getAssets().open(plant.getPhoto()), null));
            mPlantName.setText(mPlant.getName());
        }

        @Override
        public void onClick(View view) {
            if (mPlant.getPhoto().equals(DEFAULT_PHOTO)){
                AlertDialog dialog = new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.add_plant_title)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mCallbacks.onPlantSelected(mPlant); //переход к зоне самого цветка
                            }
                        })
                        .create();
                dialog.show();
            }
            else {
                mCallbacks.onPlantSelected(mPlant);
            }
        }
    }

    private class MainAdapter extends RecyclerView.Adapter<MainHolder>{
        List<Plant> mPlants;

        public MainAdapter(List<Plant> plants){
            mPlants = plants;
        }
        private void setPlants(List<Plant> plants){
            mPlants = plants;
        }
        @Override
        public MainHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = getLayoutInflater();
            return new MainHolder(inflater, parent, R.layout.fragment_main_item);
        }

        @Override
        public void onBindViewHolder(MainHolder holder, int position) {
            Plant plant = mPlants.get(position);
            try {
                holder.bind(plant);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return mPlants.size();
        }
    }
    public static Plant defaultPlant(String accountId){
        Plant plant = new Plant(UUID.randomUUID());
        plant.setPhoto(DEFAULT_PHOTO);
        plant.setIsDefault(true);
        plant.setAccountId(accountId);
        plant.setSpecies(new Species("default"));
        return plant;
    }
}
