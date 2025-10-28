package io.github.ooknight.universe.prime.web;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class R<T> implements Serializable {

    private final int code;
    private final String message;
    private final T data;
    private final Integer total;

    public R(int code, String message, T data, Integer total) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.total = total;
    }

    public static <T> R<T> success() {
        return new R<>(200, null, null, null);
    }

    public static <T> R<T> success(String message) {
        return new R<>(200, message, null, null);
    }

    public static <T> R<T> success(T data) {
        return new R<>(200, null, data, null);
    }

    public static <T> R<List<T>> success(List<T> data) {
        return new R<>(200, null, data, data == null ? null : data.size());
    }

    public static <T> R<List<T>> success(List<T> data, Integer total) {
        return new R<>(200, null, data, total);
    }

    public static <T> R<T> error() {
        return new R<>(500, null, null, null);
    }

    public static <T> R<T> error(String message) {
        return new R<>(500, message, null, null);
    }

    public static <T> R<T> error(int code, String message) {
        return new R<>(code, message, null, null);
    }

    public static <T> R<T> error(int code) {
        return new R<>(code, null, null, null);
    }

    public static <T> R<T> error(int code, String message, T data) {
        return new R<>(code, message, data, null);
    }

}
