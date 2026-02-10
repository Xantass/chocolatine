FROM python:3.14.2
WORKDIR /app

COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

COPY *.py .
COPY ./static ./static
COPY ./templates ./templates
EXPOSE 80

RUN useradd app
USER app

CMD ["flask", "run", "--host=0.0.0.0",  "--port=80"]
