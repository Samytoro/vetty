package com.example.vetty;

import android.content.Context;
import android.util.Log;

import androidx.datastore.preferences.core.MutablePreferences;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.core.PreferencesKeys;
import androidx.datastore.preferences.rxjava2.RxPreferenceDataStoreBuilder;
import androidx.datastore.rxjava2.RxDataStore;

import java.util.Objects;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class UserPreferencesManager {

    private static final String TAG = "UserPreferencesManager";
    private static final String PREF_NAME = "user_prefs";
    private static final Preferences.Key<String> USER_EMAIL_KEY = PreferencesKeys.stringKey("user_email");
    private static final Preferences.Key<String> USER_NAME_KEY = PreferencesKeys.stringKey("user_name");
    private static final Preferences.Key<String> USER_ADDRESS_KEY = PreferencesKeys.stringKey("user_address");


    private static UserPreferencesManager instance;
    private final RxDataStore<Preferences> dataStore;

    private UserPreferencesManager(Context context) {
        this.dataStore = new RxPreferenceDataStoreBuilder(context.getApplicationContext(), PREF_NAME).build();
    }

    public static synchronized UserPreferencesManager getInstance(Context context) {
        if (instance == null) {
            instance = new UserPreferencesManager(context);
        }
        return instance;
    }

    public void saveUserEmail(String email) {
        dataStore.updateDataAsync(prefs -> {
                    MutablePreferences mutablePrefs = prefs.toMutablePreferences();
                    mutablePrefs.set(USER_EMAIL_KEY, email);
                    return Single.just(mutablePrefs);
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        updatedPrefs -> Log.d(TAG, "Email guardado exitosamente: " + email),
                        throwable -> Log.e(TAG, "Error al guardar email", throwable)
                );
    }

    public Single<String> getUserEmail() {
        return dataStore.data()
                .firstOrError()
                .map(prefs -> prefs.get(USER_EMAIL_KEY));

    }


    public void saveUserName(String name) {
        dataStore.updateDataAsync(prefs -> {
                    MutablePreferences mutablePrefs = prefs.toMutablePreferences();
                    mutablePrefs.set(USER_NAME_KEY, name);
                    return Single.just(mutablePrefs);
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        updatedPrefs -> Log.d(TAG, "Nombre guardado exitosamente: " + name),
                        throwable -> Log.e(TAG, "Error al guardar nombre", throwable)
                );
    }

    public Single<String> getUserName() {
        return dataStore.data()
                .firstOrError()
                .map(prefs -> prefs.get(USER_NAME_KEY));
    }

    public void saveUserAddress(String address) {
        dataStore.updateDataAsync(prefs -> {
                    MutablePreferences mutablePrefs = prefs.toMutablePreferences();
                    mutablePrefs.set(USER_ADDRESS_KEY, address);
                    return Single.just(mutablePrefs);
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        updatedPrefs -> Log.d(TAG, "Dirección guardada exitosamente: " + address),
                        throwable -> Log.e(TAG, "Error al guardar dirección", throwable)
                );
    }

    public Single<String> getUserAddress() {
        return dataStore.data()
                .firstOrError()
                .map(prefs -> prefs.get(USER_ADDRESS_KEY));
    }
}