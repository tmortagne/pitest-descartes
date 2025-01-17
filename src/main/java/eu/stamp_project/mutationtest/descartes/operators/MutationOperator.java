package eu.stamp_project.mutationtest.descartes.operators;

import org.pitest.reloc.asm.MethodVisitor;
import org.pitest.reloc.asm.commons.Method;

import eu.stamp_project.mutationtest.descartes.operators.parsing.OperatorParser;
import eu.stamp_project.mutationtest.descartes.operators.parsing.Token;

/**
 * Mutation operator definition
 */
public abstract class MutationOperator {

    /**
     * Returns a value indicating whether the operator can transform the given method.
     *
     * @param method Method to be tested by the operator
     * @return A boolean value indicating if the mutation can be performed
     */
    public abstract boolean canMutate(Method method);

    public abstract void generateCode(Method method, MethodVisitor mv);

    public abstract String getID();

    public abstract String getDescription();

    public static MutationOperator fromID(String id) {
        OperatorParser parser = new OperatorParser(id);
        Object value = parser.parse();
        if(parser.hasErrors())
            throw new WrongOperatorException("Invalid operator id: " + parser.getErrors().get(0));
        if(value == null)
            return new NullMutationOperator();
        if (value.equals(Token.NEW.getData())) {
        	return new NewInstanceMutationOperator();
        }
        if(value.equals(Token.OPTIONAL.getData())) {
            return new OptionalMutationOperator();
        }
        if(value.equals(Void.class)) {
            return new VoidMutationOperator();
        }
        EmptyArrayMutationOperator potentialOperator = new EmptyArrayMutationOperator();
        if(value.equals(potentialOperator.getID())) {
            return potentialOperator;
        }
        try {
            return new ConstantMutationOperator(id, value);
        }catch (IllegalArgumentException exc) {
            throw new WrongOperatorException("Invalid operator id", exc);
        }
    }

}
