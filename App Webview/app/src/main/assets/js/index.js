const jogadas = ["pedra", "papel", "tesoura"];
const regras = [
  ["e", "d", "v"],
  ["v", "e", "d"],
  ["d", "v", "e"],
];
const messages = {
  e: "Empatou!",
  v: "Parabéns, você venceu!",
  d: "Você foi derrotado!",
};
const countHum = document.querySelector("#human");
const countPc = document.querySelector("#computer");
const result = document.querySelector("#result");

let hum = (pc = 0);
let imageSeted = '';

const getClickButton = (move) => {
  const jogadaPC = jogadas[Math.floor(Math.random() * jogadas.length)];
  if (!imageSeted) {
    setImage(jogadaPC);
    getResult(move, jogadaPC);
  } else{
    getResult(move, imageSeted);
  }
};

const getResult = (moveHuman, movePc) => {
  const message =
    messages[regras[jogadas.indexOf(moveHuman)][jogadas.indexOf(movePc)]];
  result.innerHTML = message;
  changeCount(message);
};

const changeCount = (message) => {
  if (message.includes("venceu")) {
    countHum.innerHTML = `${++hum}`;
  } else if (message.includes("derrotado")) {
    countPc.innerHTML = `${++pc}`;
  }
};

const setImage = (image) => {
  const ext = image.includes("white") ? "png" : "svg";
  const divImage = document.querySelector("#imagem-pc");
  divImage.src = `images/${image}.${ext}`;
};

const valueSelect = () => {
  imageSeted = '';
  const value = document.querySelector(".browser-default").value;
  if (value) {
    setImage(value);
    imageSeted = value;
  }
};

const zerarPlacar = () => {
  hum = pc = 0;
  setImage("white");
  countHum.innerHTML = countPc.innerHTML = 0;
  result.innerHTML = "Escolha uma opção:";
};
