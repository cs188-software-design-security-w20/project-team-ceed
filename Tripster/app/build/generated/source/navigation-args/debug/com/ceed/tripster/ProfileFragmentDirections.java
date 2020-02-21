package com.ceed.tripster;

import androidx.annotation.NonNull;
import androidx.navigation.ActionOnlyNavDirections;
import androidx.navigation.NavDirections;

public class ProfileFragmentDirections {
  private ProfileFragmentDirections() {
  }

  @NonNull
  public static NavDirections actionProfileFragmentToStartActivity() {
    return new ActionOnlyNavDirections(R.id.action_profileFragment_to_startActivity);
  }
}
