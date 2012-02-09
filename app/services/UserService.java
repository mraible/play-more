package services;

import securesocial.provider.SocialUser;
import securesocial.provider.UserId;
import securesocial.provider.UserService.Service;

public class UserService implements Service {
    public SocialUser find(UserId id) {
        return null;
    }

    @Override
    public void save(SocialUser user) {
    }

    @Override
    public String createActivation(SocialUser user) {
        return null;
    }

    @Override
    public boolean activate(String uuid) {
        return false;
    }

    @Override
    public void deletePendingActivations() {
    }
}
