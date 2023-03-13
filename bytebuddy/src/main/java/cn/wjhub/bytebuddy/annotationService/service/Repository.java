package cn.wjhub.bytebuddy.annotationService.service;

public abstract class Repository<T> {
    public abstract T queryData(int id);
}