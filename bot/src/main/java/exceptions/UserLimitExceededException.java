package exceptions;

public class UserLimitExceededException extends BaseException{
    public UserLimitExceededException(String message) {
        super(message);
    }
}
