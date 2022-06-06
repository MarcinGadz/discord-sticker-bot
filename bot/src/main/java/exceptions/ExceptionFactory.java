package exceptions;

public class ExceptionFactory {

    public static ImageNotFoundException imageNotFoundException() {
        return new ImageNotFoundException(ExceptionMessages.NOT_FOUND);
    }

    public static UserLimitExceededException userLimitExceededException() {
        return new UserLimitExceededException(ExceptionMessages.USER_LIMIT);
    }

    public static WrongFileTypeException wrongFileTypeException() {
        return new WrongFileTypeException(ExceptionMessages.WRONG_FILE_TYPE);
    }

    public static UnexpectedException unexpectedException() {
        return new UnexpectedException(ExceptionMessages.UNEXPECTED);
    }

}
