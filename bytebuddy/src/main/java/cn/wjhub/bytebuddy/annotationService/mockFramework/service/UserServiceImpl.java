package cn.wjhub.bytebuddy.annotationService.mockFramework.service;

public class UserServiceImpl implements UserService {
    @Override
    public void login(String username, String password) {
        System.out.println("login succeeded");
    }

    @Override
    public String getUserName(String password) {
        return null;
    }
}