package com.ceed.tripster;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.navigation.ActionOnlyNavDirections;
import androidx.navigation.NavDirections;
import java.lang.IllegalArgumentException;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.HashMap;

public class TripListFragmentDirections {
  private TripListFragmentDirections() {
  }

  @NonNull
  public static ActionTripListFragment2ToTripView actionTripListFragment2ToTripView(@NonNull String tripID) {
    return new ActionTripListFragment2ToTripView(tripID);
  }

  @NonNull
  public static NavDirections actionTripListFragment2ToProfileFragment() {
    return new ActionOnlyNavDirections(R.id.action_tripListFragment2_to_profileFragment);
  }

  @NonNull
  public static NavDirections actionTripListFragment2ToNewTripFragment() {
    return new ActionOnlyNavDirections(R.id.action_tripListFragment2_to_newTripFragment);
  }

  public static class ActionTripListFragment2ToTripView implements NavDirections {
    private final HashMap arguments = new HashMap();

    private ActionTripListFragment2ToTripView(@NonNull String tripID) {
      if (tripID == null) {
        throw new IllegalArgumentException("Argument \"tripID\" is marked as non-null but was passed a null value.");
      }
      this.arguments.put("tripID", tripID);
    }

    @NonNull
    public ActionTripListFragment2ToTripView setTripID(@NonNull String tripID) {
      if (tripID == null) {
        throw new IllegalArgumentException("Argument \"tripID\" is marked as non-null but was passed a null value.");
      }
      this.arguments.put("tripID", tripID);
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    @NonNull
    public Bundle getArguments() {
      Bundle __result = new Bundle();
      if (arguments.containsKey("tripID")) {
        String tripID = (String) arguments.get("tripID");
        __result.putString("tripID", tripID);
      }
      return __result;
    }

    @Override
    public int getActionId() {
      return R.id.action_tripListFragment2_to_tripView;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    public String getTripID() {
      return (String) arguments.get("tripID");
    }

    @Override
    public boolean equals(Object object) {
      if (this == object) {
          return true;
      }
      if (object == null || getClass() != object.getClass()) {
          return false;
      }
      ActionTripListFragment2ToTripView that = (ActionTripListFragment2ToTripView) object;
      if (arguments.containsKey("tripID") != that.arguments.containsKey("tripID")) {
        return false;
      }
      if (getTripID() != null ? !getTripID().equals(that.getTripID()) : that.getTripID() != null) {
        return false;
      }
      if (getActionId() != that.getActionId()) {
        return false;
      }
      return true;
    }

    @Override
    public int hashCode() {
      int result = 1;
      result = 31 * result + (getTripID() != null ? getTripID().hashCode() : 0);
      result = 31 * result + getActionId();
      return result;
    }

    @Override
    public String toString() {
      return "ActionTripListFragment2ToTripView(actionId=" + getActionId() + "){"
          + "tripID=" + getTripID()
          + "}";
    }
  }
}
