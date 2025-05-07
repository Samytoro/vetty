package com.example.vetty;

import android.content.Context;
import androidx.datastore.preferences.core.*;
import androidx.datastore.preferences.rxjava2.RxPreferenceDataStoreBuilder;
import androidx.datastore.rxjava2.RxDataStore;
import io.reactivex.Flowable;
import io.reactivex.Single;

public class UserPreferences {

    private static final String DATASTORE_NAME = "user_prefs";

    // Claves
    private static final Preferences.Key<String> KEY_USER_EMAIL = PreferencesKeys.stringKey("user_email");
    private static final Preferences.Key<String> KEY_USER_NAME = PreferencesKeys.stringKey("user_name");
    private static final Preferences.Key<String> KEY_USER_PHONE = PreferencesKeys.stringKey("user_phone");
    private static final Preferences.Key<String> KEY_PROFILE_PICTURE = PreferencesKeys.stringKey("profile_picture_url");
    private static final Preferences.Key<Boolean> KEY_SESSION_ACTIVE = PreferencesKeys.booleanKey("session_active");
    private static final Preferences.Key<String> KEY_LAST_MASCOTA_ID = PreferencesKeys.stringKey("last_mascota_id");
    private static final Preferences.Key<Boolean> KEY_PROFILE_COMPLETED = PreferencesKeys.booleanKey("profile_completed");

    private final RxDataStore<Preferences> dataStore;

    public UserPreferences(Context context) {
        dataStore = new RxPreferenceDataStoreBuilder(context, DATASTORE_NAME).build();
    }

    // === DATOS DE USUARIO ===
    public Flowable<String> getUserEmail() {
        return dataStore.data()
                .map(prefs -> prefs.get(KEY_USER_EMAIL));
    }

    public Single<Boolean> updateUserEmail(String email) {
        return updateString(KEY_USER_EMAIL, email);
    }

    public Flowable<String> getUserName() {
        return dataStore.data()
                .map(prefs -> prefs.get(KEY_USER_NAME));
    }

    public Single<Boolean> updateUserName(String name) {
        return updateString(KEY_USER_NAME, name);
    }

    public Flowable<String> getUserPhone() {
        return dataStore.data()
                .map(prefs -> prefs.get(KEY_USER_PHONE));
    }

    public Single<Boolean> updateUserPhone(String phone) {
        return updateString(KEY_USER_PHONE, phone);
    }



    // === SESIÓN ===
    public Flowable<Boolean> isSessionActive() {
        return dataStore.data()
                .map(prefs -> prefs.get(KEY_SESSION_ACTIVE) != null && prefs.get(KEY_SESSION_ACTIVE));
    }

    public Single<Boolean> setSessionActive(boolean isActive) {
        return updateBoolean(KEY_SESSION_ACTIVE, isActive);
    }

    // === ÚLTIMA MASCOTA CONSULTADA ===
    public Flowable<String> getLastMascotaId() {
        return dataStore.data()
                .map(prefs -> prefs.get(KEY_LAST_MASCOTA_ID));
    }

    public Single<Boolean> updateLastMascotaId(String id) {
        return updateString(KEY_LAST_MASCOTA_ID, id);
    }

    // === PERFIL COMPLETADO ===
    public Flowable<Boolean> isProfileCompleted() {
        return dataStore.data()
                .map(prefs -> prefs.get(KEY_PROFILE_COMPLETED) != null && prefs.get(KEY_PROFILE_COMPLETED));
    }

    public Single<Boolean> setProfileCompleted(boolean completed) {
        return updateBoolean(KEY_PROFILE_COMPLETED, completed);
    }



    // === MÉTODOS GENÉRICOS ===
    private Single<Boolean> updateString(Preferences.Key<String> key, String value) {
        return dataStore.updateDataAsync(prefsIn -> {
            MutablePreferences mutable = prefsIn.toMutablePreferences();
            mutable.set(key, value);
            return Single.just(mutable);
        }).map(prefsOut -> true);
    }

    private Single<Boolean> updateBoolean(Preferences.Key<Boolean> key, boolean value) {
        return dataStore.updateDataAsync(prefsIn -> {
            MutablePreferences mutable = prefsIn.toMutablePreferences();
            mutable.set(key, value);
            return Single.just(mutable);
        }).map(prefsOut -> true);
    }
}
