package com.ceed.tripster;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.navigation.NavArgs;
import java.lang.IllegalArgumentException;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.HashMap;

public class TripViewActivityArgs implements NavArgs {
  private final HashMap arguments = new HashMap();

  private TripViewActivityArgs() {
  }

  private TripViewActivityArgs(HashMap argumentsMap) {
    this.arguments.putAll(argumentsMap);
  }

  @NonNull
  @SuppressWarnings("unchecked")
  public static TripViewActivityArgs fromBundle(@NonNull Bundle bundle) {
    TripViewActivityArgs __result = new TripViewActivityArgs();
    bundle.setClassLoader(TripViewActivityArgs.class.getClassLoader());
    if (bundle.containsKey("tripID")) {
      String tripID;
      tripID = bundle.getString("tripID");
      if (tripID == null) {
        throw new IllegalArgumentException("Argument \"tripID\" is marked as non-null but was passed a null value.");
      }
      __result.arguments.put("tripID", tripID);
    } else {
      throw new IllegalArgumentException("Required argument \"tripID\" is missing and does not have an android:defaultValue");
    }
    return __result;
  }

  @SuppressWarnings("unchecked")
  @NonNull
  public String getTripID() {
    return (String) arguments.get("tripID");
  }

  @SuppressWarnings("unchecked")
  @NonNull
  public Bundle toBundle() {
    Bundle __result = new Bundle();
    if (arguments.containsKey("tripID")) {
      String tripID = (String) arguments.get("tripID");
      __result.putString("tripID", tripID);
    }
    return __result;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
        return true;
    }
    if (object == null || getClass() != object.getClass()) {
        return false;
    }
    TripViewActivityArgs that = (TripViewActivityArgs) object;
    if (arguments.containsKey("tripID") != that.arguments.containsKey("tripID")) {
      return false;
    }
    if (getTripID() != null ? !getTripID().equals(that.getTripID()) : that.getTripID() != null) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int result = 1;
    result = 31 * result + (getTripID() != null ? getTripID().hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "TripViewActivityArgs{"
        + "tripID=" + getTripID()
        + "}";
  }

  public static class Builder {
    private final HashMap arguments = new HashMap();

    public Builder(TripViewActivityArgs original) {
      this.arguments.putAll(original.arguments);
    }

    public Builder(@NonNull String tripID) {
      if (tripID == null) {
        throw new IllegalArgumentException("Argument \"tripID\" is marked as non-null but was passed a null value.");
      }
      this.arguments.put("tripID", tripID);
    }

    @NonNull
    public TripViewActivityArgs build() {
      TripViewActivityArgs result = new TripViewActivityArgs(arguments);
      return result;
    }

    @NonNull
    public Builder setTripID(@NonNull String tripID) {
      if (tripID == null) {
        throw new IllegalArgumentException("Argument \"tripID\" is marked as non-null but was passed a null value.");
      }
      this.arguments.put("tripID", tripID);
      return this;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    public String getTripID() {
      return (String) arguments.get("tripID");
    }
  }
}
