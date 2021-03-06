/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.prestosql.operator.annotations;

import io.prestosql.metadata.FunctionBinding;
import io.prestosql.metadata.FunctionDependencies;
import io.prestosql.metadata.FunctionDependencyDeclaration.FunctionDependencyDeclarationBuilder;
import io.prestosql.metadata.FunctionInvoker;
import io.prestosql.spi.function.InvocationConvention;
import io.prestosql.spi.type.TypeSignature;
import io.prestosql.sql.tree.QualifiedName;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static io.prestosql.metadata.SignatureBinder.applyBoundVariables;
import static java.util.Objects.requireNonNull;

public final class FunctionImplementationDependency
        extends ScalarImplementationDependency
{
    private final QualifiedName name;
    private final List<TypeSignature> argumentTypes;

    public FunctionImplementationDependency(QualifiedName name, List<TypeSignature> argumentTypes, Optional<InvocationConvention> invocationConvention, Class<?> type)
    {
        super(invocationConvention, type);
        this.name = requireNonNull(name, "name is null");
        this.argumentTypes = requireNonNull(argumentTypes, "argumentTypes is null");
    }

    @Override
    public void declareDependencies(FunctionDependencyDeclarationBuilder builder)
    {
        builder.addFunctionSignature(name, argumentTypes);
    }

    @Override
    protected FunctionInvoker getInvoker(FunctionBinding functionBinding, FunctionDependencies functionDependencies, Optional<InvocationConvention> invocationConvention)
    {
        List<TypeSignature> types = applyBoundVariables(argumentTypes, functionBinding);
        return functionDependencies.getFunctionSignatureInvoker(name, types, invocationConvention);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FunctionImplementationDependency that = (FunctionImplementationDependency) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(argumentTypes, that.argumentTypes);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(name, argumentTypes);
    }
}
