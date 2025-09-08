package org.sekailabs.jpaq.models.dto;

import lombok.Getter;
import org.sekailabs.jpaq.models.wrapper.PaginationWrapper;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

@Getter
public class ResponseObject<T> {
    private final T content;
    private final List<String> messages;
    private final String code;
    private final boolean success;
    private final PaginationObject pagination;
    private final Timestamp requestTime;
    private ResponseObject(Builder<T> builder) {
        this.content = builder.content;
        this.messages = builder.messages;
        this.code = builder.code;
        this.success = builder.success;
        this.pagination = builder.pagination;
        this.requestTime = builder.requestTime;
    }
    public static class Builder<T> {
        private T content;
        private List<String> messages;
        private String code;
        private boolean success;
        private PaginationObject pagination;
        private Timestamp requestTime;

        public Builder<T> content(T content) {
            this.content = content;
            return this;
        }
        public Builder<T> messages(List<String> messages) {
            this.messages = messages;
            return this;
        }
        public Builder<T> messages(String... messages) {
            this.messages = Arrays.asList(messages);
            return this;
        }
        public Builder<T> code(String code) {
            this.code = code;
            return this;
        }
        public Builder<T> success(boolean success) {
            this.success = success;
            return this;
        }
        public Builder<T> unwrapPaginationWrapper(PaginationWrapper<? extends List<?>> wrapper) {
            if (wrapper != null && wrapper.getData() != null) {
                try {
                    @SuppressWarnings("unchecked")
                    T data = (T) wrapper.getData();
                    this.content = data;
                } catch (ClassCastException e) {
                    throw new IllegalArgumentException("Invalid type for content. Expected a type compatible with T.", e);
                }
                this.pagination = wrapper.exportPaginationInfo();
            }else {
                throw new IllegalArgumentException("Invalid type: wrapper data is not a List.");
            }
            return this;
        }
        public Builder<T> pagination(PaginationObject pagination) {
            this.pagination = pagination;
            return this;
        }
        public ResponseObject<T> build() {
            requestTime = new Timestamp(System.currentTimeMillis());
            return new ResponseObject<T>(this);
        }
    }
}