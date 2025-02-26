package org.sekailabs.jpaq.models.wrapper;

import org.sekailabs.jpaq.models.constant.QueryOperatorEnum;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class QueryFieldWrapper {
    private Object value;
    private QueryOperatorEnum operator;
}
