package pe.com.dev420.router_bar.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.benites.jeral.router_bar.R;

import java.util.List;

import pe.com.dev420.router_bar.events.ClickListener;
import pe.com.dev420.router_bar.model.PubEntity;
import pe.com.dev420.router_bar.storage.network.ApiClass;
import pe.com.dev420.router_bar.storage.network.RouterApi;
import pe.com.dev420.router_bar.storage.network.entity.PubEntityRaw;
import pe.com.dev420.router_bar.storage.network.entity.PubListRaw;
import pe.com.dev420.router_bar.storage.preferences.PreferencesHelper;
import pe.com.dev420.router_bar.ui.adapters.PubAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static pe.com.dev420.router_bar.storage.preferences.PreferencesHelper.getUserSession;
import static pe.com.dev420.router_bar.storage.preferences.PreferencesHelper.signOut;
import static pe.com.dev420.router_bar.util.Constants.BAR_NAME;
import static pe.com.dev420.router_bar.util.Constants.LATITUDE;
import static pe.com.dev420.router_bar.util.Constants.LONGITUDE;

public class PubsActivity extends BaseActivity
        implements SwipeRefreshLayout.OnRefreshListener {

    private FloatingActionButton fab;
    private PubAdapter pubAdapter;
    private RecyclerView pubRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<PubEntity> pubEntities;
    private FrameLayout flayLoading;
    private Context mContext;
    private SwipeRefreshLayout swipeLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pubs);
        ui();
        loadData();
    }

    private ClickListener clickListener = new ClickListener() {
        @Override
        public void onClick(View view, int position) {

        }

        @Override
        public void onLongClick(View view, int position) {

        }

        @Override
        public void onGoGps(int position) {
            Bundle bundle = new Bundle();
            bundle.putDouble(LATITUDE, pubEntities.get(position).getAddress().getLoc().getCoordinates()[1]);
            bundle.putDouble(LONGITUDE, pubEntities.get(position).getAddress().getLoc().getCoordinates()[0]);
            bundle.putString(BAR_NAME, pubEntities.get(position).getName());
            next(MapsRouteActivity.class, bundle, false);
        }

        @Override
        public void onRemove(int position) {
            showDialogRemove(position);
        }
    };

    public void showDialogRemove(int position) {
        AlertDialog.Builder dialogRemove = new AlertDialog.Builder(this)
                .setMessage("Seguro Desea Eliminar el Bar")
                .setTitle("Confirmacion")
                .setPositiveButton(R.string.vAceptar, (dialogInterface, i) -> {
                    loading(flayLoading, true);
                    Call<PubEntityRaw> listCall = ApiClass.getRetrofit().create(RouterApi.class).deletePub(pubEntities.get(position).get_id());
                    listCall.enqueue(new Callback<PubEntityRaw>() {
                        @Override
                        public void onResponse(@NonNull Call<PubEntityRaw> call, @NonNull Response<PubEntityRaw> response) {
                            loading(flayLoading, false);
                            if (response.isSuccessful()) {
                                Toast.makeText(PubsActivity.this, "Se Elimino Correctamente", Toast.LENGTH_SHORT).show();
                                loadData();
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<PubEntityRaw> call, @NonNull Throwable t) {
                            loading(flayLoading, false);
                            Toast.makeText(PubsActivity.this, t.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });

                })
                .setNegativeButton(R.string.vCancelar, (dialogInterface, i) -> dialogInterface.dismiss())
                .setCancelable(true);
        dialogRemove.create();
        dialogRemove.show();
    }

    private void ui() {
        mContext = getApplicationContext();
        swipeLayout = findViewById(R.id.swiperefresh);
        flayLoading = findViewById(R.id.flayLoading);
        pubRecyclerView = findViewById(R.id.pubRecyclerView);
        mLayoutManager = new LinearLayoutManager(this);
        pubRecyclerView.setLayoutManager(mLayoutManager);
        pubRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(view ->
                next(InsertPubActivity.class, null, false)
        );
        swipeLayout.setColorSchemeResources(R.color.Blue, R.color.Red,
                R.color.Orange, R.color.Pink, R.color.Yellow, R.color.Black,
                R.color.Cyan);
        swipeLayout.setOnRefreshListener(this);
        this.setTitle(getUserSession(mContext));
    }

    private void loadData() {
        loading(flayLoading, true);
        Call<PubListRaw> pubRawCall = ApiClass.getRetrofit().create(RouterApi.class).listPubs();
        pubRawCall.enqueue(new Callback<PubListRaw>() {
            @Override
            public void onResponse(@NonNull Call<PubListRaw> call, @NonNull Response<PubListRaw> response) {
                loading(flayLoading, false);
                if (response.isSuccessful()) {
                    PubListRaw c = response.body();
                    assert c != null;
                    pubEntities = c.getPubEntity();
                    pubAdapter = new PubAdapter(pubEntities, PubsActivity.this.getApplicationContext(), clickListener);
                    pubRecyclerView.setAdapter(pubAdapter);
                }
            }

            @Override
            public void onFailure(@NonNull Call<PubListRaw> call, @NonNull Throwable t) {
                loading(flayLoading, false);
                System.out.println(t.getMessage());
            }
        });
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this)
                .setMessage(R.string.vTextLogaout)
                .setTitle(R.string.vCerrarSession)
                .setPositiveButton(R.string.vAceptar, (dialogInterface, i) -> {
                    signOut(this);
                    next(LoginActivity.class, null, true);

                })
                .setNegativeButton(R.string.vCancelar, (dialogInterface, i) -> dialogInterface.dismiss())
                .setCancelable(true);
        dialog.create();
        dialog.show();
    }
    @Override
    public void onRefresh() {
        Call<PubListRaw> pubRawCall = ApiClass.getRetrofit().create(RouterApi.class).listPubs();
        pubRawCall.enqueue(new Callback<PubListRaw>() {
            @Override
            public void onResponse(@NonNull Call<PubListRaw> call, @NonNull Response<PubListRaw> response) {
                if (response.isSuccessful()) {
                    pubEntities = response.body().getPubEntity();
                    pubAdapter = new PubAdapter(pubEntities, PubsActivity.this.getApplicationContext(), clickListener);
                    pubRecyclerView.setAdapter(pubAdapter);
                    swipeLayout.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(@NonNull Call<PubListRaw> call, @NonNull Throwable t) {
                swipeLayout.setRefreshing(false);
                System.out.println(t.getMessage());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.loginsignup_menu, menu);
        final MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                listByName(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                PreferencesHelper.signOut(this);
                next(LoginActivity.class, null, true);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void listByName(String Text) {
        loading(flayLoading, true);
        PubEntity entity = new PubEntity();
        entity.setName(Text);
        Call<PubListRaw> pubRawCall = ApiClass.getRetrofit().create(RouterApi.class).listPubsbyName(entity);
        pubRawCall.enqueue(new Callback<PubListRaw>() {
            @Override
            public void onResponse(Call<PubListRaw> call, Response<PubListRaw> response) {
                loading(flayLoading, false);
                if (response.isSuccessful())
                    pubEntities = response.body().getPubEntity();
                pubAdapter = new PubAdapter(pubEntities, PubsActivity.this.getApplicationContext(), clickListener);
                pubRecyclerView.setAdapter(pubAdapter);
            }

            @Override
            public void onFailure(Call<PubListRaw> call, Throwable t) {
                loading(flayLoading, false);
            }
        });
    }
}
