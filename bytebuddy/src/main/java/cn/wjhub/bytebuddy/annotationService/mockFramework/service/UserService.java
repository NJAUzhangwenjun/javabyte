package cn.wjhub.bytebuddy.annotationService.mockFramework.service;

public interface UserService {
    void login(String username, String password);

    String getUserName(String password);
}
