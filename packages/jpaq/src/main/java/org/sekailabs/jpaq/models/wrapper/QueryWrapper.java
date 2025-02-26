package org.sekailabs.jpaq.models.wrapper;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.sekailabs.jpaq.models.constant.QueryOperatorEnum;
import org.sekailabs.jpaq.utils.TextUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

import jakarta.validation.ValidationException;

public class QueryWrapper {
    private final Map<String, QueryFieldWrapper> search;
    private Pageable pageable;
    public QueryWrapper() {
        this.search = new HashMap<>();
    }

    private QueryWrapper(Map<String, QueryFieldWrapper> search, Pageable pageable) {
        this.search = search;
        this.pageable = pageable;
    }

    public Map<String, QueryFieldWrapper> search() {
        return this.search;
    }

    public QueryFieldWrapper searchByField(String field){
        return this.search.get(field);
    }

    public Pageable pagination () {
        return this.pageable;
    }
    public static class QueryWrapperBuilder {
        private final Map<String, QueryFieldWrapper> search;
        private Pageable pageable;
        public QueryWrapperBuilder() {
            this.search = new HashMap<>();
        }
        public QueryWrapperBuilder search(String queryString) {
            if (queryString == null || queryString.isEmpty()) return this;
            try {
                String decodedQuery = URLDecoder.decode(queryString, StandardCharsets.UTF_8);
                Map<String, String> queryParams = Arrays.stream(decodedQuery.split("&"))
                        .map(pair -> pair.split("="))
                        .filter(parts -> parts.length == 2)
                        .collect(Collectors.toMap(parts -> parts[0], parts -> parts[1]));

                queryParams.forEach((key, value) -> {
                    String camelKey = TextUtils.kebabToCamel(key);
                    QueryOperatorEnum type = QueryOperatorEnum.EQ;
                    Object queryValue = value;
                    if (value.startsWith("~")) {
                        type = QueryOperatorEnum.LIKE;
                        queryValue = value.substring(1);
                    } else if (value.startsWith(">=")) {
                        type = QueryOperatorEnum.GTE;
                        queryValue = value.substring(2);
                    } else if (value.startsWith("<=")) {
                        type = QueryOperatorEnum.LTE;
                        queryValue = value.substring(2);
                    } else if (value.startsWith(">")) {
                        type = QueryOperatorEnum.GT;
                        queryValue = value.substring(1);
                    } else if (value.startsWith("<")) {
                        type = QueryOperatorEnum.LT;
                        queryValue = value.substring(1);
                    } else if (value.startsWith("!")) {
                        type = QueryOperatorEnum.NE;
                        queryValue = value.substring(1);
                    } else if (value.contains(",")) {
                        type = QueryOperatorEnum.IN;
                        queryValue = Arrays.asList(value.split(","));
                    } else if (value.contains("..")) {
                        type = QueryOperatorEnum.BETWEEN;
                        String[] range = value.split("\\.\\.");
                        if (range.length == 2) {
                            queryValue = Arrays.asList(range[0], range[1]);
                        }
                    }
                    this.search.put(camelKey, QueryFieldWrapper.builder()
                                    .operator(type)
                                    .value(queryValue)
                            .build());
                });

            } catch (Exception e) {
                throw new ValidationException("The query is not valid", e);
            }
            return this;
        }

        public QueryWrapperBuilder wrapSort(Pageable ipPageable) {
            int page = ipPageable.getPageNumber();
            int pageSize = ipPageable.getPageSize();
            Sort sort = ipPageable.getSort();
            var orderList = sort.get().map(o -> {
                String prop = o.getProperty();
                Sort.Direction a = o.getDirection();
                String camelProp = TextUtils.kebabToCamel(prop);
                Order camelOrder = null;
                if(a.isAscending()) {
                    camelOrder = Order.asc(camelProp);
                }else {
                    camelOrder = Order.desc(camelProp);
                }
                return camelOrder;
            }).toList();
            Sort camelSort = Sort.by(orderList);
            this.pageable = PageRequest.of(page, pageSize, camelSort);
            return this;
        }

        public QueryWrapperBuilder pageable(Pageable pageable) {
            this.pageable = pageable;
            return this;
        }

        public QueryWrapper build() {
            if (this.pageable == null) {
                this.pageable = PageRequest.of(0, 10);
            }
            return new QueryWrapper(search, pageable);
        }
    }
    public static QueryWrapperBuilder builder() {
        return new QueryWrapperBuilder();
    }
}
