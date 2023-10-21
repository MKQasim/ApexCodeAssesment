package com.apex.codeassesment.data.local;
import com.apex.codeassesment.data.model.User;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import javax.inject.Inject;

// TODO (3 points): Convert to Kotlin
// TODO (2 point): Add tests
// TODO (1 point): Use the correct naming conventions.
public class LocalDataSource {

  // TODO (3 points): Inject all dependencies instead of instantiating them.
  private final PreferencesManager preferencesManager;
  private final Moshi moshi = new Moshi.Builder().build();

  @Inject
  public LocalDataSource(final PreferencesManager preferencesManager) {
    this.preferencesManager = preferencesManager;
  }

  @NotNull
  public User loadUser() {
    final String serializedUser = preferencesManager.loadUser();
    final JsonAdapter<User> jsonAdapter = moshi.adapter(User.class);
    try {
      // TODO (1 point): Address Android Studio warning
      final User user = jsonAdapter.fromJson(serializedUser);
      return user == null ? User.Companion.createRandom() : user;
    } catch (Exception e) {
      e.printStackTrace();
      return User.Companion.createRandom();
    }
  }

  public void saveUser(@NotNull final User user) {
    final JsonAdapter<User> jsonAdapter = moshi.adapter(User.class);
    final String serializedUser = jsonAdapter.toJson(user);
    preferencesManager.saveUser(serializedUser);
  }
}
