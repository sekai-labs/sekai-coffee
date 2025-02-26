package org.sekailabs.jpaq.repositories;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.sekailabs.jpaq.models.wrapper.PaginationWrapper;
import org.sekailabs.jpaq.models.wrapper.QueryFieldWrapper;
import org.sekailabs.jpaq.models.wrapper.QueryWrapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
@NoRepositoryBean
public interface BaseRepository <T, ID extends Serializable> extends JpaRepository<T, ID>, JpaSpecificationExecutor<T> {
    default Specification<T> queryAnySpecification(QueryWrapper queryWrapper) {
        return queryAnySpecification(queryWrapper.search());
    }
    default Specification<T> queryAnySpecification(Map<String, QueryFieldWrapper> queryWrapper) {
        return (root, query, criteriaBuilder) -> {
            if (queryWrapper == null || queryWrapper.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            Predicate[] advancedPredicates = queryWrapper.entrySet().stream().map(entry -> {
                String field = entry.getKey();
                QueryFieldWrapper wrapper = entry.getValue();
                Object value = wrapper.getValue();

                return switch (wrapper.getOperator()) {
                    case EQ -> criteriaBuilder.equal(root.get(field).as(String.class), value.toString());
                    case NE -> criteriaBuilder.notEqual(root.get(field).as(String.class), value.toString());
                    case LIKE -> criteriaBuilder.like(
                            root.get(field).as(String.class),
                            "%" + value.toString() + "%"
                    );
                    case GT -> criteriaBuilder.greaterThan(
                            root.get(field).as(String.class),
                            value.toString()
                    );
                    case LT -> criteriaBuilder.lessThan(
                            root.get(field).as(String.class),
                            value.toString()
                    );
                    case GTE -> criteriaBuilder.greaterThanOrEqualTo(
                            root.get(field).as(String.class),
                            value.toString()
                    );
                    case LTE -> criteriaBuilder.lessThanOrEqualTo(
                            root.get(field).as(String.class),
                            value.toString()
                    );
                    case IN -> {
                        if (value instanceof Collection<?> collection) {
                            yield root.get(field).as(String.class).in(
                                    collection.stream().map(Object::toString).toList()
                            );
                        }
                        yield criteriaBuilder.conjunction();
                    }
                    case BETWEEN -> {
                        if (value instanceof List<?> range && range.size() == 2) {
                            yield criteriaBuilder.between(
                                    root.get(field).as(String.class),
                                    range.get(0).toString(),
                                    range.get(1).toString()
                            );
                        }
                        yield criteriaBuilder.conjunction();
                    }
                    default -> criteriaBuilder.conjunction();
                };
            }).toArray(Predicate[]::new);
            Predicate[] defaultPredicates = createDefaultPredicate(criteriaBuilder, root, queryWrapper);
            return criteriaBuilder.or(
                    criteriaBuilder.and(advancedPredicates),
                    criteriaBuilder.and(defaultPredicates)
            );
        };
    }
    default Predicate[] createDefaultPredicate(
            CriteriaBuilder criteriaBuilder,
            Root<?> root,
            Map<String, QueryFieldWrapper> queryWrapper
    ) {
        return queryWrapper.entrySet().stream()
                .map(entry -> criteriaBuilder.equal(
                        root.get(entry.getKey()),
                        entry.getValue().getValue()
                ))
                .toArray(Predicate[]::new);
    }
    default Predicate[] createDefaultPredicate(CriteriaBuilder criteriaBuilder, Root<?> root, QueryWrapper queryWrapper) {
        return createDefaultPredicate(criteriaBuilder, root, queryWrapper.search());
    }
    default Page<T> queryAny(QueryWrapper queryWrapper, Pageable pageable) {
        Specification<T> spec = queryAnySpecification(queryWrapper);
        return findAll(spec, pageable);
    }

    default Page<T> queryAny(Map<String, QueryFieldWrapper> queryWrapper, Pageable pageable) {
        Specification<T> spec = queryAnySpecification(queryWrapper);
        return findAll(spec, pageable);
    }

    default Page<T> query(Map<String, QueryFieldWrapper> param, Pageable pageable, Function<Map<String, QueryFieldWrapper>, Specification<T>> query) {
        return findAll(query.apply(param), pageable);
    }
    default Page<T> query(QueryWrapper queryWrapper, Function<Map<String, QueryFieldWrapper>, Specification<T>> query) {
        return query(queryWrapper.search(), queryWrapper.pagination(), query);
    }
    default Page<T> query(Specification<T> query, Pageable pageable) {
        return findAll(query, pageable);
    }
    default <D extends List<?>> PaginationWrapper<D> query(Map<String, QueryFieldWrapper> param, Pageable pageable, Function<Map<String, QueryFieldWrapper>, Specification<T>> query, Function<Page<T>, PaginationWrapper<D>> mapper) {
        var entityResult = findAll(query.apply(param), pageable);
        return mapper.apply(entityResult);
    }
    default <D extends List<?>> PaginationWrapper<D> query(QueryWrapper queryWrapper, Function<Map<String, QueryFieldWrapper>, Specification<T>> query, Function<Page<T>, PaginationWrapper<D>> mapper) {
        return query(queryWrapper.search(), queryWrapper.pagination(), query, mapper);
    }
}
