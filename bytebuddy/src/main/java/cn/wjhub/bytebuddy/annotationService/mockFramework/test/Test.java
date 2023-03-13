package cn.wjhub.bytebuddy.annotationService.mockFramework.test;

import cn.wjhub.bytebuddy.annotationService.mockFramework.MockFramework;
import cn.wjhub.bytebuddy.annotationService.mockFramework.Mocked;
import cn.wjhub.bytebuddy.annotationService.mockFramework.service.UserService;

public class Test {
    public static void main(String[] args) {
        MockFramework mockFramework = new MockFramework();
        UserService userServiceMock = new UserService() {
            @Mocked("login")
            @Override
            public void login(String username, String password) {
                System.out.println("login failed");
            }

            @Override
            public String getUserName(String password) {
                return "zhangsanlisi";
            }
        };
        mockFramework.registerMock(UserService.class, userServiceMock);

        UserService userService = mockFramework.createMock(UserService.class);
        userService.login("test", "111111");
        String userName = userService.getUserName("1243234");
        System.out.println("userName = " + userName);
    }
}