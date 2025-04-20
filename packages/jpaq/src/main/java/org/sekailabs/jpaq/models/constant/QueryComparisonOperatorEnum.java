package org.sekailabs.jpaq.models.constant;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;

public enum QueryComparisonOperatorEnum {
    EQ {
        public <T extends Comparable<? super T>> Predicate build(
            CriteriaBuilder cb,
            Path<T> path,
            T val
        ) {
            return cb.equal(path, val);
        }
    },
    GT {
        public <T extends Comparable<? super T>> Predicate build(
            CriteriaBuilder cb,
            Path<T> path,
            T val
        ) {
            return cb.greaterThan(path, val);
        }
    },
    GTE {
        public <T extends Comparable<? super T>> Predicate build(
            CriteriaBuilder cb,
            Path<T> path,
            T val
        ) {
            return cb.greaterThanOrEqualTo(path, val);
        }
    },
    LT {
        public <T extends Comparable<? super T>> Predicate build(
            CriteriaBuilder cb,
            Path<T> path,
            T val
        ) {
            return cb.lessThan(path, val);
        }
    },
    LTE {
        public <T extends Comparable<? super T>> Predicate build(
            CriteriaBuilder cb,
            Path<T> path,
            T val
        ) {
            return cb.lessThanOrEqualTo(path, val);
        }
    },

    BETWEEN {
        public <T extends Comparable<? super T>> Predicate build(
            CriteriaBuilder cb,
            Path<T> path,
            T val
        ) {
            return cb.between(path, val, val);
        }
    };

    public abstract <T extends Comparable<? super T>> Predicate build(
        CriteriaBuilder cb,
        Path<T> path,
        T val
    );
}
