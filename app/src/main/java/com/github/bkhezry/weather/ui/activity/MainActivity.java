package com.github.bkhezry.weather.ui.activity;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.bkhezry.weather.R;
import com.github.bkhezry.weather.model.currentweather.CurrentWeatherResponse;
import com.github.bkhezry.weather.model.fivedayweather.ListItem;
import com.github.bkhezry.weather.service.ApiService;
import com.github.bkhezry.weather.utils.ApiClient;
import com.github.bkhezry.weather.utils.Constants;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

  @BindView(R.id.recycler_view)
  RecyclerView recyclerView;
  private FastAdapter<ListItem> mFastAdapter;
  private ItemAdapter<ListItem> mItemAdapter;
  private CompositeDisposable disposable = new CompositeDisposable();
  private String cityName = "Saqqez,IR";
  private String defaultLang = "en";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ButterKnife.bind(this);
    initRecyclerView();
    generateList();
    getCurrentWeather();
  }

  private void getCurrentWeather() {
    ApiService apiService = ApiClient.getClient(getApplicationContext()).create(ApiService.class);
    disposable.add(
        apiService.getCurrentWeather(
            cityName, Constants.UNITS, defaultLang, Constants.APP_ID)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(new DisposableSingleObserver<CurrentWeatherResponse>() {
              @Override
              public void onSuccess(CurrentWeatherResponse currentWeatherResponse) {
                Log.d("MainActivity", "onSuccess: " + currentWeatherResponse.getName());
              }

              @Override
              public void onError(Throwable e) {
                Log.e("MainActivity", "onError: " + e.getMessage());
              }
            })

    );
  }

  private void initRecyclerView() {
    LinearLayoutManager layoutManager
        = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
    recyclerView.setLayoutManager(layoutManager);
    mItemAdapter = new ItemAdapter<>();
    mFastAdapter = FastAdapter.with(mItemAdapter);
    recyclerView.setItemAnimator(new DefaultItemAnimator());
    recyclerView.setAdapter(mFastAdapter);
  }

  private void generateList() {
    //TODO will be replace with five day weather api request.
    ListItem listItem = new ListItem();
    mItemAdapter.clear();
    mItemAdapter.add(listItem);
    mItemAdapter.add(listItem);
    mItemAdapter.add(listItem);
    mItemAdapter.add(listItem);
    mItemAdapter.add(listItem);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    disposable.dispose();
  }


}
